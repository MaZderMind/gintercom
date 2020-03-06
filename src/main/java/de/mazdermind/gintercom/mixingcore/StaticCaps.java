package de.mazdermind.gintercom.mixingcore;

import org.freedesktop.gstreamer.Caps;

public class StaticCaps {
    public static final Caps RTP = Caps.fromString("application/x-rtp,clock-rate=48000,media=audio,encoding-name=L16");
    public static final Caps AUDIO_BE = Caps.fromString("audio/x-raw,format=S16BE,rate=48000,channels=1");
    public static final Caps AUDIO = Caps.fromString("audio/x-raw,format=S16LE,rate=48000,channels=1");
}
