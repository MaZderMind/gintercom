package de.mazdermind.gintercom.gstreamersupport;

/**
 * These Calculations require quite a bit of care. First, we want to Choose a Buffer-Size,
 * that together with the IP (20 Bytes), UDP (8 Bytes) and RTP (12 Bytes) Header is still
 * smaller then the MTU of the smallest supported Medium.
 * <p>
 * We choose this to be an OpenVPN Tunnel over a Base-Medium which itself has a MTU of
 * 1500 Bytes, which leaves 1427 Bytes usable MTU inside the Tunnel or 1387 usable Bytes in an
 * Audio-Buffer. Our Targer-Buffer-Size should be equal or lower then this.
 * <p>
 * Furthermore, different Elements in the GStreamer World receive their Buffer-Size in
 * different Formats. Specificly the audiomixer is problematic, as it wants its Output-Buffer
 * to be specified in nanoseconds, rather then bytes, and the relevant
 * `output-buffer-duration`-Property is an Integer64 field.
 * <p>
 * This requires our Buffer-Size, assuming Mono, 48kHz Rate and 16 Bits per Sample, to fit
 * the calculation `1000000000 / 2 / 48000 * BUFFER_SIZE` with a natural number; which rules
 * out most Buffer-Sizes.
 * <p>
 * For Example a Buffer-Size of 1300 would require us to specify a target-duration of
 * `1000000000 / 2 / 48000 * 1300 = 13541666,666666666… nanoseconds, which obviously can't be
 * represented in an Integer64 field and lead to some Buffers being 1 Sample shorter then others.
 * <p>
 * Also, the Size needs to be divisable by 2, because we can't split Samples. Looking at the
 * candidates, the largest Buffser-Size which matches all these requirements is 1386 Bytes, while
 * the second-to-largest Size would be 1380, which is quite a bit nicer to look at. So this is
 * the Target-Size for our Mono-16Bit-48kHz Buffers.
 * <p>
 * 1380 Bytes Payload + 40 Bytes Header gives UDP-Packets of 1420 Bytes.
 */
public class GstConstants {
	public static final long MTU = 1420;

	public static final long BUFFER_SIZE = 1380;
	public static final long SAMPLES_PER_BUFFER = BUFFER_SIZE / 2;
	public static final long BUFFER_DURATION_NS = SAMPLES_PER_BUFFER * 1_000_000_000 / 48000;

	public static final int LATENCY_MS = 100;
}
