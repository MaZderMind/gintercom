package de.mazdermind.gintercom.matrix.controlserver;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.matrix.ControlServerTestBase;
import de.mazdermind.gintercom.mixingcore.MixingCore;

public class AssociationMixingCoreCommandIT extends ControlServerTestBase {
	@Autowired
	private MixingCore mixingCore;

	@Test
	public void clientIsCreatedOnAssociation() {
		associateClient();

		assertThat(mixingCore.getClientNames()).containsOnly(HOST_ID);
	}

	@Test
	public void clientIsDeRemovedOnDeAssociation() {
		associateClient();
		deAssociateClient();

		assertThat(mixingCore.getClientNames()).isEmpty();
	}
}
