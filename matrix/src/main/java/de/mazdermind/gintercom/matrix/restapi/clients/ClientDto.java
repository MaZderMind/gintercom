package de.mazdermind.gintercom.matrix.restapi.clients;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;

import javax.annotation.Nullable;

import de.mazdermind.gintercom.matrix.controlserver.ClientAssociation;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ClientDto {
	private String hostId;
	private String clientModel;

	@Nullable
	private String panelId = null;

	private InetSocketAddress clientAddress;
	private LocalDateTime firstSeen;

	public ClientDto(ClientAssociation clientAssociation) {
		hostId = clientAssociation.getHostId();
		clientAddress = clientAssociation.getSocketAddress();
		firstSeen = clientAssociation.getFirstSeen();
		clientModel = clientAssociation.getClientModel();
	}

	public boolean isProvisioned() {
		return panelId != null;
	}
}
