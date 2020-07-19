package de.mazdermind.gintercom.mixingcore;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MixingCoreIT {
	private MixingCore mixingCore;

	private Panel p1;
	private Panel p2;

	private Group g1;
	private Group g2;

	@Before
	public void before() throws UnknownHostException {
		mixingCore = new MixingCore();

		p1 = mixingCore.addPanel("P1", InetAddress.getByName("127.0.0.1"), 20001, 30001);
		p2 = mixingCore.addPanel("P2", InetAddress.getByName("127.0.0.1"), 20002, 30002);

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

		assertThat(mixingCore.getPanelNames()).isEmpty();
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

		assertThat(mixingCore.getPanelNames()).isEmpty();
		assertThat(mixingCore.getGroupNames()).isEmpty();

		assertThat(mixingCore.isRunning()).isTrue();
	}

	@Test
	public void perPanelGetter() {
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
	public void panelGetter() throws UnknownHostException {
		assertThat(mixingCore.getPanelByName("P1")).isSameAs(p1);
		assertThat(mixingCore.getPanelByName("P2")).isSameAs(p2);

		assertThat(mixingCore.getPanelNames()).containsOnly("P1", "P2");

		assertThat(mixingCore.hasPanel(p1)).isTrue();
		assertThat(mixingCore.hasPanel(p2)).isTrue();

		mixingCore.removePanel(p1);

		assertThat(mixingCore.getPanelByName("P1")).isNull();
		assertThat(mixingCore.getPanelByName("P2")).isSameAs(p2);

		assertThat(mixingCore.hasPanel(p1)).isFalse();
		assertThat(mixingCore.hasPanel(p2)).isTrue();

		assertThat(mixingCore.getPanelNames()).containsOnly("P2");

		Panel p3 = mixingCore.addPanel("P3", InetAddress.getByName("127.0.0.1"), 20003, 30003);

		assertThat(mixingCore.getPanelByName("P1")).isNull();
		assertThat(mixingCore.getPanelByName("P2")).isSameAs(p2);
		assertThat(mixingCore.getPanelByName("P3")).isSameAs(p3);

		assertThat(mixingCore.hasPanel(p1)).isFalse();
		assertThat(mixingCore.hasPanel(p2)).isTrue();
		assertThat(mixingCore.hasPanel(p3)).isTrue();

		assertThat(mixingCore.getPanelNames()).containsOnly("P2", "P3");
	}
}
