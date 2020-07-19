package de.mazdermind.gintercom.clientsupport.discovery.manualconfig;

import java.net.InetAddress;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * An Instance of this Class can be used as the Return-Value for a @Bean Factory Method, which supplies manual Configuration.
 * The Factory-Method can return null to desired that no manual Configuration is intended and the normal discovery cycle should be started.
 */
public class SimpleManualMatrixAddressConfiguration implements ManualMatrixAddressConfiguration {
	private InetAddress address;
	private int port;

	public SimpleManualMatrixAddressConfiguration(InetAddress address, int port) {
		this.address = address;
		this.port = port;
	}

	@Override
	public InetAddress getAddress() {
		return address;
	}

	public SimpleManualMatrixAddressConfiguration setAddress(InetAddress address) {
		this.address = address;
		return this;
	}

	@Override
	public int getPort() {
		return port;
	}

	public SimpleManualMatrixAddressConfiguration setPort(int port) {
		this.port = port;
		return this;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("address", address)
			.append("port", port)
			.toString();
	}
}
