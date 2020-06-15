package de.mazdermind.gintercom.clientsupport.controlserver;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
@RequiredArgsConstructor
public class ControlServerClient {
	private final ClientDatagramHandler clientDatagramHandler;

	private NioEventLoopGroup workerGroup;
	private Channel channel;

	@SneakyThrows
	public void start() {
		workerGroup = new NioEventLoopGroup();
		channel = new Bootstrap()
			.group(workerGroup)
			.channel(NioDatagramChannel.class)
			.handler(clientDatagramHandler)
			.bind(0).sync()
			.channel();
	}

	@PreDestroy
	@SneakyThrows
	public void stop() {
		log.info("Shutting down UDP-Socket");
		workerGroup.shutdownGracefully();
		channel.closeFuture().await();

		workerGroup = null;
		channel = null;
	}

	public boolean isBound() {
		return channel != null;
	}

	public Channel getChannel() {
		return channel;
	}
}
