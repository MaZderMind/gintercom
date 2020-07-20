package de.mazdermind.gintercom.mixingcore;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MixingCoreIT {
	private MixingCore mixingCore;

	private Client p1;
	private Client p2;

	private Group g1;
	private Group g2;

	@Before
	public void before() throws UnknownHostException {
		mixingCore = new MixingCore();

		p1 = mixingCore.addClient("P1", InetAddress.getByName("127.0.0.1"), 20001, 30001);
		p2 = mixingCore.addClient("P2", InetAddress.getByName("127.0.0.1"), 20002, 30002);

		g1 = mixingCore.addGroup("G1");
		g2 = mixingCore.addGroup("G2");
	}

	@After
	public void after() {
		mixingCore.shutdown();
	}

	@Test
	public void shutdown() {
		p1.startReceivingFrom(g1);

		mixingCore.shutdown();

		assertThat(p1.getTxGroups()).isEmpty();
		assertThat(p1.getRxGroups()).isEmpty();

		assertThat(mixingCore.getClientNames()).isEmpty();
		assertThat(mixingCore.getGroupNames()).isEmpty();

		assertThat(mixingCore.isRunning()).isFalse();
	}

	@Test
	public void clear() {
		p1.startReceivingFrom(g1);
		p1.startReceivingFrom(g2);
		p1.startTransmittingTo(g2);

		p2.startTransmittingTo(g2);

		mixingCore.clear();

		assertThat(p1.getTxGroups()).isEmpty();
		assertThat(p1.getRxGroups()).isEmpty();

		assertThat(p2.getTxGroups()).isEmpty();
		assertThat(p2.getRxGroups()).isEmpty();

		assertThat(mixingCore.getClientNames()).isEmpty();
		assertThat(mixingCore.getGroupNames()).isEmpty();

		assertThat(mixingCore.isRunning()).isTrue();
	}

	@Test
	public void perClientGetter() {
		p1.startReceivingFrom(g1);
		p1.startReceivingFrom(g2);
		p1.startTransmittingTo(g2);

		p2.startTransmittingTo(g2);

		assertThat(p1.getRxGroups()).containsOnly(g1, g2);
		assertThat(p1.getTxGroups()).containsOnly(g2);

		assertThat(p2.getRxGroups()).isEmpty();
		assertThat(p2.getTxGroups()).containsOnly(g2);

		p1.stopReceivingFrom(g1);

		assertThat(p1.getRxGroups()).containsOnly(g2);
		assertThat(p1.getTxGroups()).containsOnly(g2);

		p1.stopTransmittingTo(g2);

		assertThat(p1.getRxGroups()).containsOnly(g2);
		assertThat(p1.getTxGroups()).isEmpty();
	}

	@Test
	public void groupGetter() {
		assertThat(mixingCore.getGroupByName("G1")).isSameAs(g1);
		assertThat(mixingCore.getGroupByName("G2")).isSameAs(g2);

		assertThat(mixingCore.getGroupNames()).containsOnly("G1", "G2");

		assertThat(mixingCore.hasGroup(g1)).isTrue();
		assertThat(mixingCore.hasGroup(g2)).isTrue();

		mixingCore.removeGroup(g1);

		assertThat(mixingCore.getGroupByName("G1")).isNull();
		assertThat(mixingCore.getGroupByName("G2")).isSameAs(g2);

		assertThat(mixingCore.hasGroup(g1)).isFalse();
		assertThat(mixingCore.hasGroup(g2)).isTrue();

		assertThat(mixingCore.getGroupNames()).containsOnly("G2");

		Group g3 = mixingCore.addGroup("G3");

		assertThat(mixingCore.getGroupByName("G1")).isNull();
		assertThat(mixingCore.getGroupByName("G2")).isSameAs(g2);
		assertThat(mixingCore.getGroupByName("G3")).isSameAs(g3);

		assertThat(mixingCore.hasGroup(g1)).isFalse();
		assertThat(mixingCore.hasGroup(g2)).isTrue();
		assertThat(mixingCore.hasGroup(g3)).isTrue();

		assertThat(mixingCore.getGroupNames()).containsOnly("G2", "G3");
	}

	@Test
	public void clientGetter() throws UnknownHostException {
		assertThat(mixingCore.getClientByName("P1")).isSameAs(p1);
		assertThat(mixingCore.getClientByName("P2")).isSameAs(p2);

		assertThat(mixingCore.getClientNames()).containsOnly("P1", "P2");

		assertThat(mixingCore.hasClient(p1)).isTrue();
		assertThat(mixingCore.hasClient(p2)).isTrue();

		mixingCore.removeClient(p1);

		assertThat(mixingCore.getClientByName("P1")).isNull();
		assertThat(mixingCore.getClientByName("P2")).isSameAs(p2);

		assertThat(mixingCore.hasClient(p1)).isFalse();
		assertThat(mixingCore.hasClient(p2)).isTrue();

		assertThat(mixingCore.getClientNames()).containsOnly("P2");

		Client p3 = mixingCore.addClient("P3", InetAddress.getByName("127.0.0.1"), 20003, 30003);

		assertThat(mixingCore.getClientByName("P1")).isNull();
		assertThat(mixingCore.getClientByName("P2")).isSameAs(p2);
		assertThat(mixingCore.getClientByName("P3")).isSameAs(p3);

		assertThat(mixingCore.hasClient(p1)).isFalse();
		assertThat(mixingCore.hasClient(p2)).isTrue();
		assertThat(mixingCore.hasClient(p3)).isTrue();

		assertThat(mixingCore.getClientNames()).containsOnly("P2", "P3");
	}
}
