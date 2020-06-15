package de.mazdermind.gintercom.matrix.controlserver;

import javax.annotation.PreDestroy;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ControlServer {
	private static final int PORT = 9999;

	private final DatagramHandler datagramHandler;
	private final MessageSender messageSender;

	private EventLoopGroup workerGroup;
	private Channel channel;

	@EventListener(ContextRefreshedEvent.class)
	public void start() throws Exception {
		log.info("Starting Server");
		workerGroup = new NioEventLoopGroup();
		channel = new Bootstrap()
			.group(workerGroup)
			.channel(NioDatagramChannel.class)
			.handler(datagramHandler)
			.bind(PORT).sync()
			.channel();

		messageSender.setChannel(channel);

		log.info("Listening for Datagrams on {}", PORT);
	}

	@PreDestroy
	public void stop() throws InterruptedException {
		log.info("Stopping Server");

		datagramHandler.stopAccepting();

		if (workerGroup != null && channel != null) {
			workerGroup.shutdownGracefully();
			channel.closeFuture().await();
		}
		log.info("Server Stopped");
	}
}
