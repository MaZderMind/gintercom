package de.mazdermind.gintercom.matrix.controlserver;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.controlserver.messages.Messages;
import de.mazdermind.gintercom.clientapi.controlserver.shared.MessageDecoder;
import de.mazdermind.gintercom.clientapi.controlserver.shared.MessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class TestControlClient {
	public static final InetSocketAddress MATRIX_ADDRESS = new InetSocketAddress("::1", 9999);

	private final MessageEncoder messageEncoder;
	private final MessageDecoder messageDecoder;

	private final BlockingQueue<Object> receivedMessages = new LinkedBlockingDeque<>();

	private NioEventLoopGroup workerGroup;
	private Channel channel;

	@SneakyThrows
	public void bind() {
		log.info("Binding to UDP-Socket");
		workerGroup = new NioEventLoopGroup();
		channel = new Bootstrap()
			.group(workerGroup)
			.channel(NioDatagramChannel.class)
			.handler(new Handler())
			.bind(0).sync()
			.channel();
	}

	@SneakyThrows
	public void shutdown() {
		log.info("Shutting down UDP-Socket");
		workerGroup.shutdownGracefully();
		channel.closeFuture().await();
	}

	@SneakyThrows
	public void transmit(Object message) {
		ByteBuffer buffer = messageEncoder.encode(message);
		log.info("Sending {}", message);

		channel.write(new DatagramPacket(Unpooled.wrappedBuffer(buffer), MATRIX_ADDRESS));
		channel.flush();
	}

	public void transmit(String message) {
		ByteBuffer messageBuffer = StandardCharsets.UTF_8.encode(message);
		channel.write(new DatagramPacket(Unpooled.wrappedBuffer(messageBuffer), MATRIX_ADDRESS));
		channel.flush();
	}

	@SneakyThrows
	public <T> T awaitMessage(Class<T> messageType) {
		Object result = receivedMessages.poll(1, TimeUnit.SECONDS);

		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(messageType);

		//noinspection unchecked
		return (T) result;
	}

	public void assertNoMoreMessages() {
		assertThat(receivedMessages).isEmpty();
	}

	private class Handler extends SimpleChannelInboundHandler<DatagramPacket> {
		@Override
		protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
			Object message = messageDecoder.decode(msg.content().nioBuffer(), Messages.MATRIX_TO_CLIENT);
			log.info("Received {}", message);
			receivedMessages.add(message);
		}
	}
}
