package de.mazdermind.gintercom.matrix.portpool;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.configuration.model.MatrixConfig;
import de.mazdermind.gintercom.matrix.configuration.model.PortPoolConfig;
import de.mazdermind.gintercom.matrix.configuration.model.PortsConfig;

public class PortAllocationManagerTest {
	private static final String HOST_ID_1 = "DEAD:BEEF";
	private static final String HOST_ID_2 = "MOFA:SOFA";

	private PortAllocationManager portAllocationManager;

	@Before
	public void prepare() {
		PortPoolConfig matrixToPanelConfig = new PortPoolConfig()
			.setStart(2000)
			.setLimit(500);

		PortPoolConfig panelToMatrixConfig = new PortPoolConfig()
			.setStart(3000)
			.setLimit(500);

		Config config = new Config()
			.setMatrixConfig(new MatrixConfig()
				.setPorts(new PortsConfig()
					.setMatrixToPanel(matrixToPanelConfig)
					.setPanelToMatrix(panelToMatrixConfig)));

		portAllocationManager = new PortAllocationManager(config);
	}

	@Test
	public void allocatesPorts() {
		PortSet portSet = portAllocationManager.allocatePortSet(HOST_ID_1);
		assertThat(portSet.getMatrixToPanel(), is(2000));
		assertThat(portSet.getPanelToMatrix(), is(3000));
	}

	@Test
	public void allocatesNewPortsForNewHostId() {
		PortSet portSet1 = portAllocationManager.allocatePortSet(HOST_ID_1);
		assertThat(portSet1.getMatrixToPanel(), is(2000));
		assertThat(portSet1.getPanelToMatrix(), is(3000));

		PortSet portSet2 = portAllocationManager.allocatePortSet(HOST_ID_2);
		assertThat(portSet2.getMatrixToPanel(), is(2001));
		assertThat(portSet2.getPanelToMatrix(), is(3001));
	}

	@Test
	public void returnsSamePortsForSameHostId() {
		PortSet portSet1 = portAllocationManager.allocatePortSet(HOST_ID_1);
		PortSet portSet2 = portAllocationManager.allocatePortSet(HOST_ID_2);

		PortSet portSet1B = portAllocationManager.allocatePortSet(HOST_ID_1);
		PortSet portSet2B = portAllocationManager.allocatePortSet(HOST_ID_2);

		assertThat(portSet1, equalTo(portSet1B));
		assertThat(portSet2, equalTo(portSet2B));
	}
}
