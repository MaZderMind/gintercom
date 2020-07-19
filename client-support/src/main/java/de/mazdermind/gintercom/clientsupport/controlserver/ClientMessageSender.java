package de.mazdermind.gintercom.clientsupport.controlserver;

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
public class ClientMessageSender {
	public final ControlServerClient controlServerClient;
	public final MessageEncoder messageEncoder;
	private InetSocketAddress matrixAddress;

	public void sendMessage(Object message) {
		try {
			ByteBuffer buffer = messageEncoder.encode(message);
			Channel channel = controlServerClient.getChannel();
			channel.write(new DatagramPacket(Unpooled.wrappedBuffer(buffer), matrixAddress));
			channel.flush();
		} catch (Exception exception) {
			log.error("Error {} while sending Message {}", exception, message);
		}
	}

	public void setTarget(InetSocketAddress matrixAddress) {
		this.matrixAddress = matrixAddress;
	}
}
