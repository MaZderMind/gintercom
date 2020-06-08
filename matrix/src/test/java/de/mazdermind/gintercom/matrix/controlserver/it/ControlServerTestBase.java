package de.mazdermind.gintercom.matrix.controlserver.it;

import java.util.Collection;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.AssociateMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.AssociatedMessage;
import de.mazdermind.gintercom.matrix.IntegrationTestBase;
import de.mazdermind.gintercom.matrix.controlserver.AssociatedClientsManager;
import de.mazdermind.gintercom.matrix.controlserver.ClientAssociation;
import de.mazdermind.gintercom.matrix.events.ClientAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.ClientDeAssociatedEvent;
import de.mazdermind.gintercom.matrix.tools.mocks.TestEventReceiver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ControlServerTestBase extends IntegrationTestBase {
	protected static final String HOST_ID_1 = "TEST-0001";
	protected static final String HOST_ID_2 = "TEST-0002";
	protected static final String HOST_ID = HOST_ID_1;

	protected TestControlClient client;

	@Autowired
	protected BeanFactory beanFactory;

	@Autowired
	protected TestEventReceiver eventReceiver;

	@Autowired
	private AssociatedClientsManager associatedClientsManager;

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
	public void assertNoMoreEvents() {
		eventReceiver.assertNoMoreEvents();
	}

	@After
	public void deAssociateAllClients() {
		Collection<ClientAssociation> associations = associatedClientsManager.getAssociations();
		log.info("At end of Test {} Client(s) associated", associations.size());
		associations.forEach(association -> {
			associatedClientsManager.deAssociate(association);
			ClientDeAssociatedEvent deAssociatedEvent = eventReceiver.awaitEvent(ClientDeAssociatedEvent.class);
			Assertions.assertThat(deAssociatedEvent.getAssociation()).isSameAs(association);
		});
	}

	protected void associateClient() {
		client.transmit(new AssociateMessage().setHostId(HOST_ID));

		client.awaitMessage(AssociatedMessage.class);
		eventReceiver.awaitEvent(ClientAssociatedEvent.class);
		eventReceiver.awaitEvent(AssociateMessage.ClientMessage.class);
	}
}
