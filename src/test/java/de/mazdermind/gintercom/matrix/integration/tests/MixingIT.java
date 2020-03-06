package de.mazdermind.gintercom.matrix.integration.tests;

import org.freedesktop.gstreamer.Gst;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import de.mazdermind.gintercom.matrix.Group;
import de.mazdermind.gintercom.matrix.Matrix;
import de.mazdermind.gintercom.matrix.MatrixFactory;
import de.mazdermind.gintercom.matrix.Panel;
import de.mazdermind.gintercom.matrix.integration.tools.rtp.RtpTestClient;
import de.mazdermind.gintercom.matrix.portpool.PortSet;
import de.mazdermind.gintercom.matrix.portpool.PortSetPool;
import de.mazdermind.gintercom.matrix.portpool.PortSetPoolFactory;

public class MixingIT {
	private Matrix matrix;
	private PortSetPool portSetPool;

	@Before
	public void before() {
		Gst.init();
		matrix = MatrixFactory.getInstance();
		portSetPool = PortSetPoolFactory.getInstance();
	}

	@Test
	public void testMixing() throws InterruptedException {
		Group group0 = matrix.addGroup("0");
		Group group1 = matrix.addGroup("1");

		PortSet ports0 = portSetPool.getNextPortSet();
		Panel panel0 = matrix.addPanel("0", "127.0.0.1", ports0);
		RtpTestClient cli0 = new RtpTestClient(ports0, "0");

		PortSet ports1 = portSetPool.getNextPortSet();
		Panel panel1 = matrix.addPanel("1", "127.0.0.1", ports1);
		RtpTestClient cli1 = new RtpTestClient(ports1, "1");

		PortSet ports2 = portSetPool.getNextPortSet();
		Panel panel2 = matrix.addPanel("2", "127.0.0.1", ports2);
		RtpTestClient cli2 = new RtpTestClient(ports2, "2");

		PortSet ports3 = portSetPool.getNextPortSet();
		Panel panel3 = matrix.addPanel("3", "127.0.0.1", ports3);
		RtpTestClient cli3 = new RtpTestClient(ports3, "3");

		panel0.startTransmittingTo(group0);
		panel1.startTransmittingTo(group1);
		panel2.startReceivingFrom(group0);
		panel3.startReceivingFrom(group1);

		cli0.enableSine(400.);
		cli1.enableSine(600.);

		cli2.getAudioAnalyser().awaitFrequencies(ImmutableSet.of(400.));
		cli3.getAudioAnalyser().awaitFrequencies(ImmutableSet.of(600.));

		cli0.stop();
		cli1.stop();
		cli2.stop();
		cli3.stop();

		panel0.remove();
		panel1.remove();
		panel2.remove();
		panel3.remove();

		group0.remove();
		group1.remove();
	}
}
