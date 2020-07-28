package de.mazdermind.gintercom.controlserver.shared;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class TestMessage {
	@NotEmpty
	private String requiredString;

	@NotNull
	private Long requiredLong;

	private Long optionalLong;

	private LocalDateTime localDateTime;
	private LocalDate localDate;

	private InetAddress inetAddressV4;
	private InetAddress inetAddressV6;

	private InetSocketAddress socketAddressV4;
	private InetSocketAddress socketAddressV6;
}
