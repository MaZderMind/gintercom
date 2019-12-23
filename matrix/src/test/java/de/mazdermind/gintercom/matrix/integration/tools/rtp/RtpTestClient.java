package de.mazdermind.gintercom.matrix.integration.tools.rtp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Scope;

import de.mazdermind.gintercom.shared.controlserver.provisioning.ProvisioningInformation;

@TestComponent
@Scope("prototype")
public class RtpTestClient {
	@Autowired
	private RtpTestClientRx rx;

	@Autowired
	private RtpTestClientTx tx;

	public void connect(ProvisioningInformation provisioningInformation) {
		connect(
			provisioningInformation.getMatrixToPanelPort(),
			provisioningInformation.getPanelToMatrixPort()
		);
	}

	private void connect(Integer matrixToPanelPort, Integer panelToMatrixPort) {
		rx.connect(matrixToPanelPort);
		tx.connect(panelToMatrixPort);
	}

	public void cleanup() {
		rx.cleanup();
		tx.cleanup();
	}

	public RtpTestClientRx getRx() {
		return rx;
	}

	public RtpTestClientTx getTx() {
		return tx;
	}
}
