package de.mazdermind.gintercom.matrix;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.AssociationRequestMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.DeAssociationRequestMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.AssociatedMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.DeAssociatedMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.DeProvisionMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.ProvisionMessage;
import de.mazdermind.gintercom.matrix.controlserver.AssociatedClientsManager;
import de.mazdermind.gintercom.matrix.controlserver.ClientAssociation;
import de.mazdermind.gintercom.matrix.controlserver.TestControlClient;
import de.mazdermind.gintercom.matrix.events.ClientAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.ClientDeAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.PanelGroupsChangedEvent;
import de.mazdermind.gintercom.matrix.tools.mocks.TestEventReceiver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ControlServerTestBase extends IntegrationTestBase {
	protected TestControlClient client;

	@Autowired
	protected BeanFactory beanFactory;

	@Autowired
	protected TestEventReceiver eventReceiver;

	@Autowired
	protected AssociatedClientsManager associatedClientsManager;

	@Before
	public void createAndBindTestClient() {
		client = beanFactory.getBean(TestControlClient.class);
		client.bind();
	}

	@After
	public void shutdownTestClient() {
		client.shutdown();
		client.assertNoMoreMessages();
	}

	@After
	public void deAssociateAllClients() {
		eventReceiver.assertNoMoreEvents();

		Collection<ClientAssociation> associations = associatedClientsManager.getAssociations();
		log.info("At end of Test {} Client(s) associated", associations.size());
		associations.forEach(association -> {
			associatedClientsManager.deAssociate(association, "Test Cleanup");
		});

		eventReceiver.clear();
	}

	protected ClientAssociation associateClient(String clientId) {
		client.transmit(new AssociationRequestMessage().setClientId(clientId));

		client.awaitMessage(AssociatedMessage.class);
		client.maybeAwaitMessage(ProvisionMessage.class);

		ClientAssociatedEvent associatedEvent = eventReceiver.awaitEvent(ClientAssociatedEvent.class);
		eventReceiver.maybeAwaitEvent(PanelGroupsChangedEvent.class);
		eventReceiver.awaitEvent(AssociationRequestMessage.ClientMessage.class);

		return associatedEvent.getAssociation();
	}

	protected void deAssociateClient() {
		client.transmit(new DeAssociationRequestMessage().setReason("Test-Request"));

		eventReceiver.awaitEvent(ClientDeAssociatedEvent.class);
		eventReceiver.awaitEvent(DeAssociationRequestMessage.ClientMessage.class);
		client.maybeAwaitMessage(DeProvisionMessage.class);
		client.awaitMessage(DeAssociatedMessage.class);
	}
}
