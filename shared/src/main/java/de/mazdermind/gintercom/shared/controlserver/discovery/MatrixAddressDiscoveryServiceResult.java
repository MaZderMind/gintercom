package de.mazdermind.gintercom.shared.controlserver.discovery;

import java.net.InetAddress;

public class MatrixAddressDiscoveryServiceResult {
	private static final int WELL_KNOWN_PORT = 2380;

	private InetAddress address;

	private int port;

	public MatrixAddressDiscoveryServiceResult(InetAddress address, int port) {
		this.address = address;
		this.port = port;
	}

	public MatrixAddressDiscoveryServiceResult(InetAddress address) {
		this(address, WELL_KNOWN_PORT);
	}

	public InetAddress getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}
}
