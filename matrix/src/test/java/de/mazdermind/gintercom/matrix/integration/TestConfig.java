package de.mazdermind.gintercom.matrix.integration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Primary;

import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.configuration.model.MatrixConfig;
import de.mazdermind.gintercom.matrix.configuration.model.PortPoolConfig;
import de.mazdermind.gintercom.matrix.configuration.model.PortsConfig;
import de.mazdermind.gintercom.matrix.configuration.model.RtpConfig;
import de.mazdermind.gintercom.matrix.configuration.model.ServerConfig;

@TestComponent
@Primary
public class TestConfig extends Config {
	public TestConfig() {
		reset();
	}

	public void reset() {
		try {
			setMatrixConfig(new MatrixConfig()
				.setDisplay("")
				.setPorts(new PortsConfig()
					.setMatrixToPanel(new PortPoolConfig()
						.setStart(40000)
						.setLimit(1000))
					.setPanelToMatrix(new PortPoolConfig()
						.setStart(50000)
						.setLimit(1000)))
				.setRtp(new RtpConfig()
					.setJitterbuffer(100L))
				.setWebui(new ServerConfig()
					.setBind(InetAddress.getLocalHost())));
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}

		setButtonsets(new HashMap<>());
		setGroups(new HashMap<>());
		setPanels(new HashMap<>());
	}
}
