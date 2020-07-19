package de.mazdermind.gintercom.mixingcore.tools.peakdetector;

import java.nio.ByteBuffer;

import org.freedesktop.gstreamer.Buffer;
import org.freedesktop.gstreamer.Sample;

public class AppSinkSupport {
	public static long[] extractSampleValues(Sample sample) {
		Buffer buffer = sample.getBuffer();
		ByteBuffer bytes = buffer.map(false);

		int numSamples = bytes.capacity() / Double.BYTES;
		long[] samples = new long[numSamples];
		bytes.asLongBuffer().get(samples);

		buffer.unmap();
		sample.dispose();

		return samples;
	}
}
