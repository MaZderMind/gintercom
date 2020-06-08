package de.mazdermind.gintercom.matrix.tools.mocks;

import static de.mazdermind.gintercom.matrix.tools.RandomGroupConfigBuilder.randomGroupConfig;
import static de.mazdermind.gintercom.matrix.tools.RandomPanelConfigBuilder.randomPanelConfig;

import java.util.HashMap;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Primary;

import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.configuration.model.GroupConfig;
import de.mazdermind.gintercom.matrix.configuration.model.MatrixConfig;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
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
				.setBind(null)
				.setPort(0)));

		setButtonsets(new HashMap<>());
		setGroups(new HashMap<>());
		setPanels(new HashMap<>());
	}

	public PanelConfig addRandomPanel() {
		PanelConfig panelConfig = randomPanelConfig();
		getPanels().put(panelConfig.getDisplay(), panelConfig);
		return panelConfig;
	}

	public GroupConfig addRandomGroup() {
		GroupConfig groupConfig = randomGroupConfig();
		getGroups().put(groupConfig.getDisplay(), groupConfig);
		return groupConfig;
	}
}
