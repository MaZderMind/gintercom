package de.mazdermind.gintercom.matrix.controlserver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationEventPublisher;

import de.mazdermind.gintercom.matrix.events.ClientAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.ClientDeAssociatedEvent;
import de.mazdermind.gintercom.matrix.portpool.PortAllocationManager;
import de.mazdermind.gintercom.matrix.tools.mocks.TestConfig;

public class AssociatedClientsManagerTest {
	private static final InetSocketAddress ADDRESS_1 = new InetSocketAddress("10.73.100.42", 9999);
	private static final InetSocketAddress ADDRESS_2 = new InetSocketAddress("10.73.100.23", 9999);

	private static final String HOST_ID_1 = "0000-0000";
	private static final String HOST_ID_2 = "0000-0001";

	private AssociatedClientsManager associatedClientsManager;
	private ApplicationEventPublisher eventPublisher;

	@Before
	public void before() {
		eventPublisher = mock(ApplicationEventPublisher.class);
		associatedClientsManager = new AssociatedClientsManager(new PortAllocationManager(new TestConfig()), eventPublisher);
	}

	@Test
	public void canAssociateClient() {
		ClientAssociation association = associatedClientsManager.associate(ADDRESS_1, HOST_ID_1);
		assertThat(associatedClientsManager.isAssociated(ADDRESS_1)).isTrue();
		assertThat(associatedClientsManager.isAssociated(HOST_ID_1)).isTrue();
		assertThat(associatedClientsManager.getAssociations()).hasSize(1);

		assertThat(associatedClientsManager.getAssociation(ADDRESS_1)).isNotNull().isSameAs(association);
		assertThat(associatedClientsManager.getAssociation(HOST_ID_1)).isNotNull().isSameAs(association);

		assertThat(association.getHostId()).isEqualTo(HOST_ID_1);
		assertThat(association.getSocketAddress()).isEqualTo(ADDRESS_1);
		assertThat(association.getFirstSeen()).isCloseTo(LocalDateTime.now(), within(5, ChronoUnit.SECONDS));
		assertThat(association.getLastHeartbeat()).isEqualTo(association.getFirstSeen());
		assertThat(association.getRtpPorts().getMatrixToPanel()).isNotNegative().isNotZero();
		assertThat(association.getRtpPorts().getPanelToMatrix()).isNotNegative().isNotZero();

		verify(eventPublisher).publishEvent(any(ClientAssociatedEvent.class));
	}

	@Test(expected = AssociatedClientsManager.SocketAddressAlreadyAssociatedException.class)
	public void cantAssociateSameSocketAddressTwice() {
		associatedClientsManager.associate(ADDRESS_1, HOST_ID_1);
		associatedClientsManager.associate(ADDRESS_1, HOST_ID_2);
	}

	@Test(expected = AssociatedClientsManager.HostIdAlreadyAssociatedException.class)
	public void cantAssociateSameHostId() {
		associatedClientsManager.associate(ADDRESS_1, HOST_ID_1);
		associatedClientsManager.associate(ADDRESS_2, HOST_ID_1);
	}

	@Test
	public void canDeAssociateClient() {
		associatedClientsManager.associate(ADDRESS_1, HOST_ID_1);
		associatedClientsManager.deAssociate(HOST_ID_1, "Test");

		assertThat(associatedClientsManager.isAssociated(ADDRESS_1)).isFalse();
		assertThat(associatedClientsManager.isAssociated(HOST_ID_1)).isFalse();
		assertThat(associatedClientsManager.getAssociations()).hasSize(0);

		verify(eventPublisher).publishEvent(any(ClientDeAssociatedEvent.class));
	}

	@Test(expected = AssociatedClientsManager.NotAssociatedException.class)
	public void cantDeAssociateUnAssociatedClient() {
		associatedClientsManager.deAssociate(HOST_ID_1, "Test");
	}

	@Test
	public void canListAssociatedClients() {
		associatedClientsManager.associate(ADDRESS_1, HOST_ID_1);
		associatedClientsManager.associate(ADDRESS_2, HOST_ID_2);

		assertThat(associatedClientsManager.getAssociations()).hasSize(2);
		assertThat(associatedClientsManager.getAssociations())
			.extracting(ClientAssociation::getHostId)
			.contains(HOST_ID_1, HOST_ID_2);
		assertThat(associatedClientsManager.getAssociations())
			.extracting(ClientAssociation::getSocketAddress)
			.contains(ADDRESS_1, ADDRESS_2);
	}

	@Test
	public void canGetAssociationByHostId() {
		associatedClientsManager.associate(ADDRESS_1, HOST_ID_1);

		assertThat(associatedClientsManager.getAssociation(HOST_ID_1)).isNotNull()
			.extracting(ClientAssociation::getSocketAddress)
			.isSameAs(ADDRESS_1);
	}

	@Test
	public void canGetAssociationBySocketAddress() {
		associatedClientsManager.associate(ADDRESS_1, HOST_ID_1);

		assertThat(associatedClientsManager.getAssociation(ADDRESS_1)).isNotNull()
			.extracting(ClientAssociation::getHostId)
			.isSameAs(HOST_ID_1);
	}

	@Test(expected = AssociatedClientsManager.NotAssociatedException.class)
	public void getAssociationThrowsExceptionForNonExistingAssociationByHostId() {
		associatedClientsManager.getAssociation(HOST_ID_1);
	}

	@Test(expected = AssociatedClientsManager.NotAssociatedException.class)
	public void getAssociationThrowsExceptionForNonExistingAssociationBySocketAddress() {
		associatedClientsManager.getAssociation(ADDRESS_1);
	}

	@Test
	public void canFindAssociationByHostId() {
		associatedClientsManager.associate(ADDRESS_1, HOST_ID_1);

		assertThat(associatedClientsManager.findAssociation(HOST_ID_1)).isPresent().get()
			.extracting(ClientAssociation::getSocketAddress)
			.isSameAs(ADDRESS_1);
	}

	@Test
	public void canFindAssociationBySocketAddress() {
		associatedClientsManager.associate(ADDRESS_1, HOST_ID_1);

		assertThat(associatedClientsManager.findAssociation(ADDRESS_1)).isPresent().get()
			.extracting(ClientAssociation::getHostId)
			.isSameAs(HOST_ID_1);
	}

	@Test
	public void findAssociationReturnsEmptyForNonExistingAssociationByHostId() {
		assertThat(associatedClientsManager.findAssociation(HOST_ID_1)).isEmpty();
	}

	@Test
	public void findAssociationReturnsEmptyForNonExistingAssociationBySocketAddress() {
		assertThat(associatedClientsManager.findAssociation(ADDRESS_1)).isEmpty();
	}
}
