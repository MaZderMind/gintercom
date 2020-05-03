package de.mazdermind.gintercom.clientapi.messages.provision;

import java.net.InetAddress;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AlreadyRegisteredMessage {
	private InetAddress remoteIp;
	private LocalDateTime connectionTime;
}
