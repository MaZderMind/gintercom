#!/usr/bin/env python
import logging
from threading import Thread, Event

rtp_max_jitter_mx = 30

from tools.application_init import application_init

application_init()

from gi.repository import Gst, GLib
from tools.runner import Runner

log = logging.getLogger("main")

log.info("building pipeline")
pipeline = Gst.Pipeline.new()
log.debug(pipeline)

caps_audio = Gst.Caps.from_string("audio/x-raw,format=S16LE,rate=48000,channels=2")
caps_audio_be = Gst.Caps.from_string("audio/x-raw,format=S16BE,rate=48000,channels=2")
caps_rtp = Gst.Caps.from_string("application/x-rtp,clock-rate=48000,media=audio,encoding-name=L16,channels=2")

Gst.debug_bin_to_dot_file_with_ts(pipeline, Gst.DebugGraphDetails.ALL, "startup")

stop_event = Event()


class GroupBin(Gst.Bin):
    def __init__(self, name):
        super().__init__(name=name)

        silence_src = Gst.ElementFactory.make("audiotestsrc")
        silence_src.set_property("freq", 440)
        # silence_src.set_property("volume", 0.1)
        silence_src.set_property("is-live", True)
        self.add(silence_src)

        self.mixer = mixer = Gst.ElementFactory.make("audiomixer")
        self.add(mixer)

        log.debug(silence_src.link_filtered(mixer, caps_audio))

        self.tee = tee = Gst.ElementFactory.make("tee", "grp-tee")
        tee.set_property("allow-not-linked", True)
        self.add(tee)

        log.debug(mixer.link(tee))


    def request_src_pad(self, name):
        templ = self.tee.get_pad_template("src_%u")
        tee_pad = self.tee.request_pad(templ)
        ghost_pad = Gst.GhostPad.new(name, tee_pad)
        ghost_pad.set_active(True)
        self.add_pad(ghost_pad)
        return ghost_pad

    def release_src_pad(self, ghost_pad):
        tee_pad = ghost_pad.get_target()
        self.remove_pad(ghost_pad)
        self.tee.release_request_pad(tee_pad)

    def request_sink_pad(self, name):
        templ = self.mixer.get_pad_template("sink_%u")
        mixer_pad = self.mixer.request_pad(templ)
        ghost_pad = Gst.GhostPad.new(name, mixer_pad)
        ghost_pad.set_active(True)
        self.add_pad(ghost_pad)
        return ghost_pad

    def release_sink_pad(self, ghost_pad):
        mixer_pad = ghost_pad.get_target()
        self.remove_pad(ghost_pad)
        self.mixer.release_request_pad(mixer_pad)


def add_group_bin():
    log.info("Adding Group-Bin to the Pipeline")

    grpbin = GroupBin("grp-bin")
    pipeline.add(grpbin)
    grpbin.sync_state_with_parent()

    Gst.debug_bin_to_dot_file_with_ts(pipeline, Gst.DebugGraphDetails.ALL, "add_group_bin")
    log.info("Added Group-Bin to the Pipeline")


def remove_group_bin():
    log.info("Removing Group-Bin from the Pipeline")

    grpbin = pipeline.get_by_name("grp-bin")
    log.info("Stopping Group-Bin")
    log.debug(grpbin.set_state(Gst.State.NULL))

    # TODO unlink panels if not unlinked yet

    pipeline.remove(grpbin)
    Gst.debug_bin_to_dot_file_with_ts(pipeline, Gst.DebugGraphDetails.ALL, "remove_group_bin")
    log.info("Removed Group-Bin from the Pipeline")


class RxBin(Gst.Bin):
    def __init__(self, name, port):
        super().__init__(name=name)

        src = Gst.ElementFactory.make("audiotestsrc")
        src.set_property("wave", "square")
        src.set_property("is-live", True)
        self.add(src)

        self.tee = tee = Gst.ElementFactory.make("tee")
        tee.set_property("allow-not-linked", True)
        self.add(tee)
        src.link_filtered(tee, caps_audio)

    def request_src_pad(self, name):
        templ = self.tee.get_pad_template("src_%u")
        tee_pad = self.tee.request_pad(templ)
        ghost_pad = Gst.GhostPad.new(name, tee_pad)
        ghost_pad.set_active(True)
        self.add_pad(ghost_pad)
        return ghost_pad

    def release_src_pad(self, ghost_pad):
        tee_pad = ghost_pad.get_target()
        self.remove_pad(ghost_pad)
        self.tee.release_request_pad(tee_pad)


class TxBin(Gst.Bin):
    def __init__(self, name, port):
        super().__init__(name=name)

        silence_src = Gst.ElementFactory.make("audiotestsrc")
        silence_src.set_property("is-live", True)
        silence_src.set_property("freq", 220)
        # silence_src.set_property("volume", 0.1)
        self.add(silence_src)

        self.mixer = mixer = Gst.ElementFactory.make("audiomixer")
        self.add(mixer)
        silence_src.link_filtered(mixer, caps_audio)

        sink = Gst.ElementFactory.make("autoaudiosink")
        self.add(sink)
        mixer.link(sink)

    def request_sink_pad(self, name):
        templ = self.mixer.get_pad_template("sink_%u")
        mixer_pad = self.mixer.request_pad(templ)
        ghost_pad = Gst.GhostPad.new(name, mixer_pad)
        ghost_pad.set_active(True)
        self.add_pad(ghost_pad)
        return ghost_pad

    def release_sink_pad(self, ghost_pad):
        mixer_pad = ghost_pad.get_target()
        self.remove_pad(ghost_pad)
        self.mixer.release_request_pad(mixer_pad)


def add_panel_bin(i):
    log.info("Adding RX-Bin for Panel %d to the Pipeline" % i)

    rxbin = RxBin("rx-bin-%d" % i, 15000 + 1)
    pipeline.add(rxbin)
    rxbin.sync_state_with_parent()

    log.info("Added RX-Bin for Panel %d to the Pipeline" % i)
    log.info("Adding TX-Bin for Panel %d to the Pipeline" % i)

    txbin = TxBin("tx-bin-%d" % i, 16000 + 1)
    pipeline.add(txbin)
    txbin.sync_state_with_parent()

    log.info("Added TX-Bin for Panel %d to the Pipeline" % i)
    Gst.debug_bin_to_dot_file_with_ts(pipeline, Gst.DebugGraphDetails.ALL, "add_panel_bin_%u" % i)


def link_panel_tx(i):
    log.info("Linking Group to TX-Bin for Panel %d" % i)

    txbin = pipeline.get_by_name("tx-bin-%d" % i)
    txpad = txbin.request_sink_pad(name="grp")

    grpbin = pipeline.get_by_name("grp-bin")
    grppad = grpbin.request_src_pad(name="panel-tx-%d" % i)

    log.debug("grppad=%s, txpad=%s, link=%s", grppad, txpad, grppad.link(txpad))

    log.info("Linked Group to TX-Bin for Panel %d" % i)
    Gst.debug_bin_to_dot_file_with_ts(pipeline, Gst.DebugGraphDetails.ALL, "link_panel_tx_%u" % i)


def link_panel_rx(i):
    log.info("Linking RX-Bin for Panel %d to Group" % i)

    rxbin = pipeline.get_by_name("rx-bin-%d" % i)
    rxpad = rxbin.request_src_pad(name="grp")

    grpbin = pipeline.get_by_name("grp-bin")
    grppad = grpbin.request_sink_pad(name="panel-rx-%d" % i)

    log.debug("rxpad=%s grppad=%s, link=%s", rxpad, grppad, rxpad.link(grppad))

    log.info("Linked RX-Bin for Panel %d to Group" % i)
    Gst.debug_bin_to_dot_file_with_ts(pipeline, Gst.DebugGraphDetails.ALL, "link_panel_rx_%u" % i)


def unlink_panel_rx(i):
    rxbin = pipeline.get_by_name("rx-bin-%d" % i)
    grpbin = pipeline.get_by_name("grp-bin")

    rxpad = rxbin.get_static_pad("grp")
    grppad = grpbin.get_static_pad("panel-rx-%d" % i)

    def blocking_pad_probe(pad, info):
        log.info("Pad in IDLE state")
        rxpad.unlink(grppad)

        rxbin.release_src_pad(rxpad)
        grpbin.release_sink_pad(grppad)

        Gst.debug_bin_to_dot_file_with_ts(pipeline, Gst.DebugGraphDetails.ALL, "unlink_panel_rx_%u" % i)

        return Gst.PadProbeReturn.REMOVE

    rxpad.add_probe(Gst.PadProbeType.IDLE, blocking_pad_probe)


def unlink_panel_tx(i):
    txbin = pipeline.get_by_name("tx-bin-%d" % i)
    grpbin = pipeline.get_by_name("grp-bin")

    txpad = txbin.get_static_pad("grp")
    grppad = grpbin.get_static_pad("panel-tx-%d" % i)

    def blocking_pad_probe(pad, info):
        log.info("Pad in IDLE state")
        grppad.unlink(txpad)

        txbin.release_sink_pad(txpad)
        grpbin.release_src_pad(grppad)

        Gst.debug_bin_to_dot_file_with_ts(pipeline, Gst.DebugGraphDetails.ALL, "unlink_panel_tx_%u" % i)

        return Gst.PadProbeReturn.REMOVE

    grppad.add_probe(Gst.PadProbeType.IDLE, blocking_pad_probe)


def remove_panel_bin(i):
    log.info("Removing RX-Bin for Panel %d to the Pipeline" % i)

    rxbin = pipeline.get_by_name("rx-bin-%d" % i)
    rxbin.set_state(Gst.State.NULL)
    pipeline.remove(rxbin)

    txbin = pipeline.get_by_name("tx-bin-%d" % i)
    txbin.set_state(Gst.State.NULL)
    pipeline.remove(txbin)

    # TODO block and remove tx-bin

    Gst.debug_bin_to_dot_file_with_ts(pipeline, Gst.DebugGraphDetails.ALL, "remove_panel_bin_%u" % i)
    log.info("Removed RX-Bins for Panel %d to the Pipeline" % i)


def timed_sequence():
    log.info("Starting Sequence")

    timeout_seconds = 0.2
    num_ports = 1
    while True:
        if stop_event.wait(timeout_seconds): return
        log.info("Scheduling add_group_bin")
        GLib.idle_add(add_group_bin)

        for i in range(0, num_ports):
            if stop_event.wait(timeout_seconds): return
            log.info("Scheduling add_panel_bin for Panel %d", i)
            GLib.idle_add(add_panel_bin, i)

        for n in range(0, 2):
            for i in range(0, num_ports):
                if stop_event.wait(timeout_seconds): return
                log.info("Scheduling link_panel_tx for Panel %d", i)
                GLib.idle_add(link_panel_tx, i)

            for i in range(0, num_ports):
                if stop_event.wait(timeout_seconds): return
                log.info("Scheduling link_panel_rx for Panel %d", i)
                GLib.idle_add(link_panel_rx, i)

            for i in range(0, num_ports):
                if stop_event.wait(timeout_seconds): return
                log.info("Scheduling unlink_panel_rx for Panel %d", i)
                GLib.idle_add(unlink_panel_rx, i)

            for i in range(0, num_ports):
                if stop_event.wait(timeout_seconds): return
                log.info("Scheduling unlink_panel_tx for Panel %d", i)
                GLib.idle_add(unlink_panel_tx, i)

        for i in range(0, num_ports):
            if stop_event.wait(timeout_seconds): return
            log.info("Scheduling remove_panel_bin for Panel %d", i)
            GLib.idle_add(remove_panel_bin, i)

        if stop_event.wait(timeout_seconds): return
        log.info("Scheduling remove_group_bin")
        GLib.idle_add(remove_group_bin)


t = Thread(target=timed_sequence, name="Sequence")
t.start()

runner = Runner(pipeline)
runner.run_blocking()

stop_event.set()
t.join()
