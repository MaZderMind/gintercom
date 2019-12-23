package de.mazdermind.gintercom.matrix.controlserver.panelregistration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class PanelConnectionManager {
	private final Map<String, PanelConnectionInformation> sessionIdToHostIdMap = new HashMap<>();

	public boolean isHostIdAlreadyRegistered(String hostId) {
		return sessionIdToHostIdMap.values().stream()
			.anyMatch(information -> information.getHostId().equals(hostId));
	}

	public PanelConnectionInformation getHostIdForSessionId(String sessionId) {
		return sessionIdToHostIdMap.get(sessionId);
	}

	public String getSessionIdForHostId(String hostId) {
		return sessionIdToHostIdMap.entrySet().stream()
			.filter(entry -> entry.getValue().getHostId().equals(hostId))
			.findFirst()
			.map(Map.Entry::getKey)
			.orElse(null);
	}

	public void registerPanelConnection(String sessionId, PanelConnectionInformation panelConnectionInformation) {
		if (isHostIdAlreadyRegistered(panelConnectionInformation.getHostId())) {
			throw new IllegalArgumentException("Host-ID is already registered");
		}
		sessionIdToHostIdMap.put(sessionId, panelConnectionInformation);
	}

	public PanelConnectionInformation deregisterPanelConnection(String sessionId) {
		return sessionIdToHostIdMap.remove(sessionId);
	}

	public Optional<PanelConnectionInformation> getConnectionInformationForHostId(String hostId) {
		return sessionIdToHostIdMap.values().stream()
			.filter(information -> information.getHostId().equals(hostId))
			.findFirst();
	}

}
