package de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client;

import javax.validation.constraints.NotNull;

import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.AssociationRequestMessage;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * An AssociatedMessage is sent from the Matrix to a Client as a response to an {@link AssociationRequestMessage}.
 * <p>
 * The AssociatedMessage signals to the Client, that the Matrix has allocated an RTP-Endpoint for it ans is already sending an RTP-Stream
 * to it. The Port where this Stream is sent to is denoted by the {@link #rtpMatrixToPanelPort} port and the Client is
 * expected to start receiving this stream on the given port.
 * <p>
 * Also the Matrix is already awaiting a reponse-Stream by the Client on the Port {@link #rtpPanelToMatrixPort}. The Client is expected
 * to send a stream to this port.
 * <p>
 * There are no strong timing requirements as to when the Client has to start receiving or sending Data; it can take as long as it needs
 * to or, in special cases, even decide to not send an Audio-Stream at all.
 */
@Data
@Accessors(chain = true)
public class AssociatedMessage {
	@NotNull
	private Integer rtpMatrixToPanelPort;

	@NotNull
	private Integer rtpPanelToMatrixPort;
}
