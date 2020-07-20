package de.mazdermind.gintercom.matrix.portpool;

import static org.assertj.core.api.Assertions.assertThat;

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
		PortPoolConfig matrixToClientConfig = new PortPoolConfig()
			.setStart(2000)
			.setLimit(500);

		PortPoolConfig clientToMatrixConfig = new PortPoolConfig()
			.setStart(3000)
			.setLimit(500);

		Config config = new Config()
			.setMatrixConfig(new MatrixConfig()
				.setPorts(new PortsConfig()
					.setMatrixToClient(matrixToClientConfig)
					.setClientToMatrix(clientToMatrixConfig)));

		portAllocationManager = new PortAllocationManager(config);
	}

	@Test
	public void allocatesPorts() {
		PortSet portSet = portAllocationManager.allocatePortSet(HOST_ID_1);
		assertThat(portSet.getMatrixToClient()).isEqualTo(2000);
		assertThat(portSet.getClientToMatrix()).isEqualTo(3000);
	}

	@Test
	public void allocatesNewPortsForNewClientId() {
		PortSet portSet1 = portAllocationManager.allocatePortSet(HOST_ID_1);
		assertThat(portSet1.getMatrixToClient()).isEqualTo(2000);
		assertThat(portSet1.getClientToMatrix()).isEqualTo(3000);

		PortSet portSet2 = portAllocationManager.allocatePortSet(HOST_ID_2);
		assertThat(portSet2.getMatrixToClient()).isEqualTo(2001);
		assertThat(portSet2.getClientToMatrix()).isEqualTo(3001);
	}

	@Test
	public void returnsSamePortsForSameClientId() {
		PortSet portSet1 = portAllocationManager.allocatePortSet(HOST_ID_1);
		PortSet portSet2 = portAllocationManager.allocatePortSet(HOST_ID_2);

		PortSet portSet1B = portAllocationManager.allocatePortSet(HOST_ID_1);
		PortSet portSet2B = portAllocationManager.allocatePortSet(HOST_ID_2);

		assertThat(portSet1).isEqualTo(portSet1B);
		assertThat(portSet2).isEqualTo(portSet2B);
	}
}
