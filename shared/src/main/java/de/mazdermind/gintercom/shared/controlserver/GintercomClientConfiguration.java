package de.mazdermind.gintercom.shared.controlserver;

import java.util.List;

/**
 * By Implementing a Bean providing this Interface, an Application enables the ConnectionLifecycleManager which uses this Information to
 * announce its existence to the Matrix and requests Provisioning.
 */
public interface GintercomClientConfiguration {
	String getClientId();

	Integer getProtocolVersion();

	String getClientModel();

	List<String> getButtons();
}
