package de.mazdermind.gintercom.matrix.controlserver;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.controlserver.shared.MessageEncoder;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageSender {
	private final MessageEncoder messageEncoder;
	private final AssociatedClientsManager associatedClientsManager;

	private Channel channel;

	public void sendMessageTo(String hostId, Object message) {
		ClientAssociation association = associatedClientsManager.getAssociation(hostId);
		sendMessageTo(association.getSocketAddress(), message);
	}

	void sendMessageTo(InetSocketAddress address, Object message) {
		try {
			ByteBuffer encode = messageEncoder.encode(message);
			channel.write(new DatagramPacket(Unpooled.wrappedBuffer(encode), address));
			channel.flush();
		} catch (Exception exception) {
			log.error("Error {} while sending Message {}", exception, message);
		}
	}

	void setChannel(Channel channel) {
		this.channel = channel;
	}
}
