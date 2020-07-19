package de.mazdermind.gintercom.clientsupport.discovery.manualconfig;

import java.net.InetAddress;

/**
 * Implement this Interface, either with a @Configuration Class implementing this Interface permanently
 * or by implementing a @Configuration Class with a @Bean annotated Factory-Method. The Factory-Method can
 * return null to desired that no manual Configuration is intended and the normal discovery cycle should be started.
 * <p>
 * You can also return an Instance of the SimpleManualMatrixAddressConfiguration from your Factory, which
 * implements this interface for you.
 */
public interface ManualMatrixAddressConfiguration {
	InetAddress getAddress();

	int getPort();
}
