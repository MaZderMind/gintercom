package de.mazdermind.gintercom.clientsupport.controlserver;

import java.nio.ByteBuffer;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.controlserver.messages.Messages;
import de.mazdermind.gintercom.clientapi.controlserver.shared.MessageDecoder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@ChannelHandler.Sharable
public class ClientDatagramHandler extends SimpleChannelInboundHandler<DatagramPacket> {
	private final MessageDecoder messageDecoder;
	private final ApplicationEventPublisher eventPublisher;

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket packet) {
		ByteBuffer buffer = packet.content().nioBuffer();

		try {
			Object message = messageDecoder.decode(buffer, Messages.MATRIX_TO_CLIENT);
			eventPublisher.publishEvent(message);
		} catch (Exception e) {
			log.warn("Exception handling received Message", e);
		}
	}
}
