package de.mazdermind.gintercom.clientsupport.pipeline;

import java.net.InetAddress;

public interface ClientPipeline {
	void configurePipeline(InetAddress matrixAddress, int matrixToClientPort, int clientToMatrixPort);

	void startPipeline();

	void destroyPipeline();
}
