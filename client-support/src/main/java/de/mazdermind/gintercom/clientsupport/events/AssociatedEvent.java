package de.mazdermind.gintercom.clientsupport.events;

import java.net.InetSocketAddress;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AssociatedEvent {
	private InetSocketAddress matrixAddress;

	private Integer rtpMatrixToClientPort;
	private Integer rtpClientToMatrixPort;
}
