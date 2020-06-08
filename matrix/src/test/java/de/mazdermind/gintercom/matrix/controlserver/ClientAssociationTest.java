package de.mazdermind.gintercom.matrix.controlserver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.Before;
import org.junit.Test;

public class ClientAssociationTest {
	private ClientAssociation clientAssociation;

	@Before
	public void before() {
		clientAssociation = new ClientAssociation();
	}

	@Test
	public void initializesTimestamps() {
		assertThat(clientAssociation.getLastHeartbeat()).isCloseTo(LocalDateTime.now(), within(5, ChronoUnit.SECONDS));
		assertThat(clientAssociation.getFirstSeen()).isEqualTo(clientAssociation.getLastHeartbeat());
		assertThat(clientAssociation.isTimedOut()).isFalse();
	}

	@Test
	public void canTimeout() {
		clientAssociation.setLastHeartbeat(LocalDateTime.of(2000, 1, 1, 10, 0, 0));
		assertThat(clientAssociation.isTimedOut()).isTrue();
	}

	@Test
	public void canRegisterHeartbeat() {
		clientAssociation.setLastHeartbeat(LocalDateTime.of(2000, 1, 1, 10, 0, 0));
		assertThat(clientAssociation.isTimedOut()).isTrue();
		clientAssociation.registerHeartbeat();
		assertThat(clientAssociation.isTimedOut()).isFalse();
		assertThat(clientAssociation.getLastHeartbeat()).isCloseTo(LocalDateTime.now(), within(5, ChronoUnit.SECONDS));
	}

}
