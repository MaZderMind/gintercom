package de.mazdermind.gintercom.debugclient.controlserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.shared.controlserver.ControlServerClient;

@Component
public class Configurer {
	public Configurer(@Autowired ControlServerClient controlserverClient) {
		controlserverClient.connect();
	}
}
