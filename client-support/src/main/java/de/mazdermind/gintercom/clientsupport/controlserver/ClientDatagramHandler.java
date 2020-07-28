package de.mazdermind.gintercom.clientsupport.controlserver;

import java.nio.ByteBuffer;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.controlserver.shared.MessageDecoder;
import de.mazdermind.gintercom.clientsupport.events.BeforeClientShutdownEvent;
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
	private static final String MESSAGE_PACKAGE = "de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client";

	private final MessageDecoder messageDecoder;
	private final ApplicationEventPublisher eventPublisher;
	private boolean shutdown = false;

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket packet) {
		if (shutdown) {
			return;
		}

		try {
			ByteBuffer buffer = packet.content().nioBuffer();
			Object message = messageDecoder.decode(buffer, MESSAGE_PACKAGE);
			eventPublisher.publishEvent(message);
		} catch (Exception e) {
			log.warn("Exception handling received Message", e);
		}
	}

	@EventListener(BeforeClientShutdownEvent.class)
	public void handleBeforeShutdownEvent() {
		shutdown = true;
	}
}
