package de.mazdermind.gintercom.matrix.tools.mocks;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Primary;

import com.oblac.nomen.Nomen;

import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.configuration.model.GroupConfig;
import de.mazdermind.gintercom.matrix.configuration.model.MatrixConfig;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.configuration.model.PortPoolConfig;
import de.mazdermind.gintercom.matrix.configuration.model.PortsConfig;
import de.mazdermind.gintercom.matrix.configuration.model.RtpConfig;
import de.mazdermind.gintercom.matrix.configuration.model.ServerConfig;
import de.mazdermind.gintercom.mixingcore.MixingCore;

@TestComponent
@Primary
public class TestConfig extends Config {
	@Autowired
	private MixingCore mixingCore;

	public TestConfig() {
		reset();
	}

	public void reset() {
		setMatrixConfig(new MatrixConfig()
			.setDisplay("")
			.setPorts(new PortsConfig()
				.setMatrixToClient(new PortPoolConfig()
					.setStart(40000)
					.setLimit(1000))
				.setClientToMatrix(new PortPoolConfig()
					.setStart(50000)
					.setLimit(1000)))
			.setRtp(new RtpConfig()
				.setJitterbuffer(100L))
			.setWebui(new ServerConfig()
				.setBind(null)
				.setPort(0)));

		setButtonSets(new HashMap<>());
		setGroups(new HashMap<>());
		setPanels(new HashMap<>());
	}

	public String addRandomPanel() {
		String panelId = Nomen.randomName().toLowerCase();

		PanelConfig panelConfig = new PanelConfig()
			.setDisplay(panelId);

		getPanels().put(panelId, panelConfig);
		return panelId;
	}

	public String addRandomPanel(String clientId) {
		String panelId = Nomen.randomName().toLowerCase();

		PanelConfig panelConfig = new PanelConfig()
			.setClientId(clientId)
			.setDisplay(panelId);

		getPanels().put(panelId, panelConfig);
		return panelId;
	}

	public String addRandomGroup() {
		String groupId = Nomen.randomName().toLowerCase();

		GroupConfig groupConfig = new GroupConfig()
			.setDisplay(groupId);

		getGroups().put(groupId, groupConfig);
		return groupId;
	}

	public String addRandomGroupToMixingCore() {
		String groupId = addRandomGroup();
		mixingCore.addGroup(groupId);
		return groupId;
	}
}
