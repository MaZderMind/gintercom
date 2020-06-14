package de.mazdermind.gintercom.matrix.controlserver;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.controlserver.messages.Messages;
import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.AssociateMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.ErrorMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.wrapper.WrappedClientMessage;
import de.mazdermind.gintercom.clientapi.controlserver.shared.ClientMessageWrapper;
import de.mazdermind.gintercom.clientapi.controlserver.shared.MessageDecoder;
import de.mazdermind.gintercom.clientapi.controlserver.shared.MessageEncoder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatagramHandler extends SimpleChannelInboundHandler<DatagramPacket> {
	private final MessageDecoder messageDecoder;
	private final MessageEncoder messageEncoder;
	private final ClientMessageWrapper clientMessageWrapper;
	private final AssociatedClientsManager associatedClientsManager;
	private final ApplicationEventPublisher eventPublisher;

	private boolean accepting = true;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) {
		if (!accepting) {
			log.warn("Rejecting Message (probably due to ongoing shutdown)");
			return;
		}

		ByteBuffer buffer = packet.content().nioBuffer();
		InetSocketAddress sender = packet.sender();

		try {
			handlePacket(ctx, sender, buffer);
		} catch (Exception e) {
			respondWithError(ctx, e.toString(), sender);
		}
	}

	private void handlePacket(ChannelHandlerContext ctx, InetSocketAddress sender, ByteBuffer buffer) throws Exception {
		Object message;

		message = messageDecoder.decode(buffer, Messages.CLIENT_TO_MATRIX);

		if (message instanceof AssociateMessage) {
			try {
				AssociateMessage associateMessage = (AssociateMessage) message;
				associatedClientsManager.associate(sender, associateMessage.getHostId());
			} catch (Exception e) {
				respondWithError(ctx, e.getMessage(), sender);
				return;
			}
		}

		Optional<ClientAssociation> association = associatedClientsManager.findAssociation(sender);
		if (!association.isPresent()) {
			respondWithError(ctx, String.format("The Socket-Address %s is not not associated", sender), sender);
			return;
		}

		String hostId = association.get().getHostId();
		WrappedClientMessage<Object> clientMessage = clientMessageWrapper.wrap(message, hostId);
		eventPublisher.publishEvent(clientMessage);
	}

	private void respondWithError(ChannelHandlerContext ctx, String error, InetSocketAddress sender) {
		ErrorMessage message = new ErrorMessage().setMessage(error);
		try {
			ByteBuffer buffer = messageEncoder.encode(message);
			ctx.write(new DatagramPacket(Unpooled.wrappedBuffer(buffer), sender));
			ctx.flush();
		} catch (Exception followupException) {
			log.error("Error {} while responding with Error-Message {}", followupException, error);
		}
	}

	public void stopAccepting() {
		accepting = false;
	}
}
