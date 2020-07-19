package de.mazdermind.gintercom.clientsupport.discovery;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MatrixAddressDiscoveryServiceResult {
	private static final int WELL_KNOWN_PORT = 9999;

	private final InetSocketAddress socketAddress;

	public MatrixAddressDiscoveryServiceResult(InetSocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}

	public MatrixAddressDiscoveryServiceResult(InetAddress address) {
		this(new InetSocketAddress(address, WELL_KNOWN_PORT));
	}
}
