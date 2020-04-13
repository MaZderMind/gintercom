package de.mazdermind.gintercom.mixingcore.support;

import java.util.EnumSet;

import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Pipeline;

public class GstDebugger {
	private static final EnumSet<Bin.DebugGraphDetails> DETAILS = EnumSet.of(
		Bin.DebugGraphDetails.SHOW_STATES,
		Bin.DebugGraphDetails.SHOW_NON_DEFAULT_PARAMS,
		Bin.DebugGraphDetails.SHOW_MEDIA_TYPE,
		Bin.DebugGraphDetails.SHOW_CAPS_DETAILS);

	private static int index = 0;

	public static void debugPipeline(String filename, Pipeline pipeline) {
		pipeline.debugToDotFile(Bin.DebugGraphDetails.SHOW_ALL, (index++) + "-" + filename);
	}
}
