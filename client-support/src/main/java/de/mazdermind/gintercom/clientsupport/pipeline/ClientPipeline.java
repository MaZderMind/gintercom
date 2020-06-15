package de.mazdermind.gintercom.clientsupport.pipeline;

import java.net.InetAddress;

public interface ClientPipeline {
	void configurePipeline(InetAddress matrixAddress, int matrixToPanelPort, int panelToMatrixPort);

	void startPipeline();

	void destroyPipeline();
}
