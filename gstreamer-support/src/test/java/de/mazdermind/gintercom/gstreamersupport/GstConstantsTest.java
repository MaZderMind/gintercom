package de.mazdermind.gintercom.gstreamersupport;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class GstConstantsTest {
	private static final int IP_HEADER_SIZE = 20;
	private static final int UDP_HEADER_SIZE = 8;
	private static final int RTP_HEADER_SIZE = 12;
	private static final int HEADER_SIZE = IP_HEADER_SIZE + UDP_HEADER_SIZE + RTP_HEADER_SIZE;

	@Test
	public void samplesPerBufferCanBeCalculatedAsRegularNumber() {
		double samplesPerBufferAsFloat = ((double) GstConstants.BUFFER_SIZE) / 2.;
		assertThat(samplesPerBufferAsFloat).isEqualTo((double) GstConstants.SAMPLES_PER_BUFFER);
	}

	@Test
	public void bufferDurationCaBeCalculatedAsRegularNumber() {
		double bufferDurationAsFloat = ((double) GstConstants.SAMPLES_PER_BUFFER) * 1_000_000_000. / 48000.;
		assertThat(bufferDurationAsFloat).isEqualTo((double) GstConstants.BUFFER_DURATION_NS);
	}

	@Test
	public void mtuMatchesBufferSize() {
		assertThat(GstConstants.BUFFER_SIZE + HEADER_SIZE).isEqualTo(GstConstants.MTU);
	}
}
