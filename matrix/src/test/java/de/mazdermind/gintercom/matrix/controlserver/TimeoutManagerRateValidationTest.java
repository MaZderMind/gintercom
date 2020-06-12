package de.mazdermind.gintercom.matrix.controlserver;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.Test;

public class TimeoutManagerRateValidationTest {
	// Lower Gap = Higher Rate

	@Test
	public void sendingRateMustBeHigherThenTimeout() {
		Duration sendingGap = Duration.parse(TimeoutManager.HEARTBEAT_SENDING_GAP);
		assertThat(sendingGap).isLessThan(ClientAssociation.HEARTBEAT_TIMEOUT);
	}

	@Test
	public void validationRateMustBeHigherThenTimeout() {
		Duration validationGap = Duration.parse(TimeoutManager.HEARTBEAT_VALIDATION_GAP);
		assertThat(validationGap).isLessThan(ClientAssociation.HEARTBEAT_TIMEOUT);
	}
}
