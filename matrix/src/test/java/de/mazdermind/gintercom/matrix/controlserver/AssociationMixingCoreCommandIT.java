package de.mazdermind.gintercom.matrix.controlserver;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.matrix.ControlServerTestBase;
import de.mazdermind.gintercom.matrix.tools.TestClientIdGenerator;
import de.mazdermind.gintercom.mixingcore.Client;
import de.mazdermind.gintercom.mixingcore.MixingCore;

public class AssociationMixingCoreCommandIT extends ControlServerTestBase {
	@Autowired
	private MixingCore mixingCore;
	private String clientId;

	@Before
	public void before() {
		clientId = TestClientIdGenerator.generateTestClientId();
	}

	@Test
	public void clientIsCreatedOnAssociation() {
		associateClient(clientId);

		assertThat(mixingCore.getClients())
			.extracting(Client::getId)
			.containsOnly(clientId);
	}

	@Test
	public void clientIsDeRemovedOnDeAssociation() {
		associateClient(clientId);
		deAssociateClient();

		assertThat(mixingCore.getClients()).isEmpty();
	}
}
