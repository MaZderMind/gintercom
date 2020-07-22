package de.mazdermind.gintercom.mixingcore;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import de.mazdermind.gintercom.mixingcore.tools.IntegrationTestBase;
import de.mazdermind.gintercom.mixingcore.tools.ClientInfo;

public class MixingIT extends IntegrationTestBase {
	/**
	 * 1 Group, 1 Client
	 * Client 1 transmits to Group 1
	 * Client 1 receives from Group 1
	 * assert that Client 1 hears itself
	 */
	@Test
	public void clientTransmittingIntoAGroupItIsAlsoReceivingFromHearsItsOwnAudio() {
		Group group1 = testManager.addGroup("1");
		ClientInfo client1 = testManager.addClient("1");

		client1.getClientEntity().startTransmittingTo(group1);
		client1.getClientEntity().startReceivingFrom(group1);

		client1.getRtpClient().enableSine(800.);
		client1.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(800.));
	}

	/**
	 * 1 Group, 2 Clients
	 * Client 1 transmits to Group 1
	 * Client 2 receives from Group 1
	 * assert that Client 2 hears Client 1
	 */
	@Test
	public void clientReceivingFromAGroupHearsAudioTransmittedFromAnotherClientIntoThisGroup() {
		Group group1 = testManager.addGroup("1");
		ClientInfo client1 = testManager.addClient("1");
		ClientInfo client2 = testManager.addClient("2");

		client1.getClientEntity().startTransmittingTo(group1);
		client2.getClientEntity().startReceivingFrom(group1);

		client1.getRtpClient().enableSine(2000.);
		client2.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(2000.));
		client1.getRtpClient().getAudioAnalyser().awaitSilence();
	}

	/**
	 * 1 Group, 3 Clients
	 * Client 1 transmits to Group 1
	 * Client 2 receives from Group 1
	 * Client 3 receives from Group 1
	 * assert that Client 2 hears Client 1
	 * Client 3 joins
	 * assert that Client 3 also hears Client 1
	 * assert that Client 2 still hears Client 1
	 * Client 3 leaves
	 * assert that Client 2 still hears Client 1
	 */
	@Test
	public void clientCanJoinAndLeaveGroupWithoutDisturbingOtherClients() {
		Group group1 = testManager.addGroup("1");
		ClientInfo client1 = testManager.addClient("1");
		ClientInfo client2 = testManager.addClient("2");

		client1.getClientEntity().startTransmittingTo(group1);
		client2.getClientEntity().startReceivingFrom(group1);

		client1.getRtpClient().enableSine(2500.);
		client2.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(2500.));

		ClientInfo client3 = testManager.addClient("3");
		client3.getClientEntity().startReceivingFrom(group1);
		client3.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(2500.));

		client2.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(2500.));

		client3.stopAndRemove();

		client2.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(2500.));
	}

	/**
	 * 2 Groups, 2 Clients
	 * Client 1 receives from Group 1
	 * Client 1 transmits to Group 2
	 * Client 2 transmits to Group 1
	 * Client 2 receives from Group 2
	 * assert that the clients hear each other but not them self
	 */
	@Test
	public void clientTransmittingIntoAGroupItIsNotReceivingFromDoesNotHearItsOwnAudio() {
		Group group1 = testManager.addGroup("1");
		Group group2 = testManager.addGroup("2");

		ClientInfo client1 = testManager.addClient("1");
		ClientInfo client2 = testManager.addClient("2");

		client1.getClientEntity().startReceivingFrom(group1);
		client1.getClientEntity().startTransmittingTo(group2);

		client2.getClientEntity().startReceivingFrom(group2);
		client2.getClientEntity().startTransmittingTo(group1);

		client1.getRtpClient().enableSine(800.);
		client2.getRtpClient().enableSine(400.);

		client1.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(400.));
		client2.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(800.));
	}

	/**
	 * 2 Groups, 3 Client
	 * Client 1 transmits to Group 1, Group 2
	 * Client 2 receives from Group 1
	 * Client 3 receives from Group 2
	 * assert that Client 2 and Client 3 both hear Client 1
	 */
	@Test
	public void clientTransmittingIntoMultipleGroupsIsHeardInAllOfThem() {
		Group group1 = testManager.addGroup("1");
		Group group2 = testManager.addGroup("2");

		ClientInfo client1 = testManager.addClient("1");
		ClientInfo client2 = testManager.addClient("2");
		ClientInfo client3 = testManager.addClient("3");

		client1.getClientEntity().startTransmittingTo(group1);
		client1.getClientEntity().startTransmittingTo(group2);
		client1.getRtpClient().enableSine(600.);

		client2.getClientEntity().startReceivingFrom(group1);
		client3.getClientEntity().startReceivingFrom(group2);

		client2.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(600.));
		client3.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(600.));
	}


	/**
	 * 2 Groups, 3 Client
	 * Client 1 receives from Group 1, Group 2
	 * Client 2 transmits to Group 1
	 * Client 3 transmits to Group 2
	 * assert that Client 1 hears both Client 2 and Client 3
	 */
	@Test
	public void clientReceivingMultipleGroupsHearsAudioFromAllOfThem() {
		Group group1 = testManager.addGroup("1");
		Group group2 = testManager.addGroup("2");

		ClientInfo client1 = testManager.addClient("1");
		ClientInfo client2 = testManager.addClient("2");
		ClientInfo client3 = testManager.addClient("3");

		client1.getClientEntity().startReceivingFrom(group1);
		client1.getClientEntity().startReceivingFrom(group2);

		client2.getClientEntity().startTransmittingTo(group1);
		client3.getClientEntity().startTransmittingTo(group2);

		client2.getRtpClient().enableSine(1000.);
		client3.getRtpClient().enableSine(2000.);

		client1.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(1000., 2000.));
	}

	/**
	 * 2 Groups, 4 Clients
	 * Client 1 transmits to Group 1
	 * Client 2 receives from Group 1
	 * Client 3 transmits to Group 2
	 * Client 4 transmits to Group 2
	 * assert that Client 2 hears Client 1 and Client 4 hears Client 3 but nothing else
	 */
	@Test
	public void clientsCanCommunicateInParallel() {
		Group group1 = testManager.addGroup("1");
		Group group2 = testManager.addGroup("2");

		ClientInfo client1 = testManager.addClient("1");
		ClientInfo client2 = testManager.addClient("2");
		ClientInfo client3 = testManager.addClient("3");
		ClientInfo client4 = testManager.addClient("4");

		client1.getClientEntity().startTransmittingTo(group1);
		client2.getClientEntity().startTransmittingTo(group2);
		client3.getClientEntity().startReceivingFrom(group1);
		client4.getClientEntity().startReceivingFrom(group2);

		client1.getRtpClient().enableSine(400.);
		client2.getRtpClient().enableSine(600.);

		client3.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(400.));
		client4.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(600.));
	}

	/**
	 * 1 Group, 3 Clients
	 * Client 1 transmits to Group
	 * Client 3 receives from Group
	 * assert that Client 3 hears Client 1
	 * Client 2 starts transmitting to Group
	 * assert that Client 3 hears Client 1 and 2
	 * Client 2 stops transmitting to Group
	 * assert that Client 3 hears Client 1
	 * Client 2 starts transmitting to Group
	 * assert that Client 3 hears Client 1 and 2
	 */
	@Test
	public void clientStartAndStopTransmittingToAGroup() {
		Group group1 = testManager.addGroup("1");

		ClientInfo client1 = testManager.addClient("1");
		ClientInfo client2 = testManager.addClient("2");
		ClientInfo rxClient = testManager.addClient("3");

		rxClient.getClientEntity().startReceivingFrom(group1);

		client1.getRtpClient().enableSine(1000.);
		client2.getRtpClient().enableSine(3000.);

		rxClient.getRtpClient().getAudioAnalyser().awaitSilence();

		client1.getClientEntity().startTransmittingTo(group1);
		rxClient.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(1000.));

		client2.getClientEntity().startTransmittingTo(group1);
		rxClient.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(1000., 3000.));

		client1.getClientEntity().stopTransmittingTo(group1);
		rxClient.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(3000.));

		client2.getClientEntity().stopTransmittingTo(group1);
		rxClient.getRtpClient().getAudioAnalyser().awaitSilence();

		client1.getClientEntity().startTransmittingTo(group1);
		rxClient.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(1000.));

		client2.getClientEntity().startTransmittingTo(group1);
		rxClient.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(1000., 3000.));
	}

	@Test
	public void clientStartAndStopReceivingAGroup() {
		Group group1 = testManager.addGroup("1");
		Group group2 = testManager.addGroup("2");

		ClientInfo client1 = testManager.addClient("1");
		ClientInfo client2 = testManager.addClient("2");
		ClientInfo rxClient = testManager.addClient("3");

		client1.getClientEntity().startTransmittingTo(group1);
		client1.getRtpClient().enableSine(300.);

		client2.getClientEntity().startTransmittingTo(group2);
		client2.getRtpClient().enableSine(600.);

		rxClient.getRtpClient().getAudioAnalyser().awaitSilence();

		rxClient.getClientEntity().startReceivingFrom(group1);
		rxClient.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(300.));

		rxClient.getClientEntity().startReceivingFrom(group2);
		rxClient.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(300., 600.));

		rxClient.getClientEntity().stopReceivingFrom(group1);
		rxClient.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(600.));

		rxClient.getClientEntity().stopReceivingFrom(group2);
		rxClient.getRtpClient().getAudioAnalyser().awaitSilence();

		rxClient.getClientEntity().startReceivingFrom(group1);
		rxClient.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(300.));

		rxClient.getClientEntity().startReceivingFrom(group2);
		rxClient.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(300., 600.));

	}

	@Test
	public void groupCanBeRemovedWhileClientsAreConnected() {
		Group group1 = testManager.addGroup("1");
		Group group2 = testManager.addGroup("2");

		ClientInfo txClient = testManager.addClient("tx");
		ClientInfo rxClient1 = testManager.addClient("rx1");
		ClientInfo rxClient2 = testManager.addClient("rx2");

		txClient.getClientEntity().startTransmittingTo(group1);
		txClient.getClientEntity().startTransmittingTo(group2);

		rxClient1.getClientEntity().startReceivingFrom(group1);
		rxClient2.getClientEntity().startReceivingFrom(group2);

		txClient.getRtpClient().enableSine(600.);
		rxClient1.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(600.));
		rxClient2.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(600.));

		testManager.getMixingCore().removeGroup(group1);

		rxClient1.getRtpClient().getAudioAnalyser().awaitSilence();
		rxClient2.getRtpClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(600.));
	}
}
