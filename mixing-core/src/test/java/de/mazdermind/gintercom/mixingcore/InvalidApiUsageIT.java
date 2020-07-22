package de.mazdermind.gintercom.mixingcore;

import static org.junit.Assert.assertThrows;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import de.mazdermind.gintercom.mixingcore.exception.InvalidMixingCoreOperationException;
import de.mazdermind.gintercom.mixingcore.tools.IntegrationTestBase;
import de.mazdermind.gintercom.mixingcore.tools.ClientInfo;

/**
 * This Test ensures that the Pipeline does not crash when the API is used in an invalid way and that other Clients and Groups are still
 * operable. The Mis-Used Clients and Groups are not expected to be functional after misusing the API with them.
 */
public class InvalidApiUsageIT extends IntegrationTestBase {
	private Client client;
	private Group group;
	private MixingCore mixingCore;

	@Before
	public void before() {
		client = testManager.addClient("p").getClientEntity();
		group = testManager.addGroup("g");
		mixingCore = testManager.getMixingCore();
	}

	@Test
	public void toleratesInvalidOrderOfOperation() {
		assertThrows(InvalidMixingCoreOperationException.class, () -> client.stopTransmittingTo(group));
		assertThrows(InvalidMixingCoreOperationException.class, () -> client.stopTransmittingTo(group));

		assertThrows(InvalidMixingCoreOperationException.class, () -> client.stopReceivingFrom(group));
		assertThrows(InvalidMixingCoreOperationException.class, () -> client.stopReceivingFrom(group));

		ensurePipelineIsStillFunctional();
	}

	@Test
	public void toleratesMultipleRemoval() {
		mixingCore.removeClient(client);
		assertThrows(InvalidMixingCoreOperationException.class, () -> mixingCore.removeClient(client));

		mixingCore.removeGroup(group);
		assertThrows(InvalidMixingCoreOperationException.class, () -> mixingCore.removeGroup(group));

		ensurePipelineIsStillFunctional();
	}

	@Test
	public void toleratesDuplicateNames() {
		assertThrows(InvalidMixingCoreOperationException.class, () -> testManager.addClient("p"));
		assertThrows(InvalidMixingCoreOperationException.class, () -> testManager.addGroup("g"));

		ensurePipelineIsStillFunctional();
	}

	private void ensurePipelineIsStillFunctional() {
		ClientInfo newClient = testManager.addClient("p-new");
		Group newGroup = testManager.addGroup("g-new");

		newClient.getClientEntity().startReceivingFrom(newGroup);
		newClient.getClientEntity().startTransmittingTo(newGroup);

		newClient.getRtpClient().enableSine(880.);
		newClient.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(880.));
	}
}
