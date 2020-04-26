package de.mazdermind.gintercom.mixingcore;

public class Constants {
	public static final int HEADER_SIZE = 54;
	public static final int BYTES_PER_SAMPLE = 2;
	public static final int SAMPLE_RATE = 48000;

	public static final int MTU = 1500;
	public static final int SAMPLES_PER_BUFFER = (MTU - HEADER_SIZE) / BYTES_PER_SAMPLE; // 1473
	public static final long BUFFER_DURATION_NS = (long) SAMPLES_PER_BUFFER * 1_000_000_000 / SAMPLE_RATE; // 30687500

	public static final int LATENCY_MS = 100;
}
