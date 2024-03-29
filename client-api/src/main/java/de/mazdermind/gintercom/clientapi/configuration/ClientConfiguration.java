package de.mazdermind.gintercom.clientapi.configuration;

import java.util.List;

/**
 * By Implementing a Bean providing this Interface, an Application enables the ConnectionLifecycleManager which uses this Information to
 * announce its existence to the Matrix and requests Provisioning.
 */
public interface ClientConfiguration {
	String getClientId();

	String getClientModel();

	List<String> getButtons();
}
