package de.mazdermind.gintercom.controlserver.shared;

import static org.assertj.core.api.Java6Assertions.assertThat;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import de.mazdermind.gintercom.clientapi.controlserver.shared.MessageEncoder;
import lombok.Data;
import lombok.experimental.Accessors;

public class MessageEncoderTest {
	private MessageEncoder messageEncoder;

	@Before
	public void before() {
		ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		messageEncoder = new MessageEncoder(objectMapper, validator);
	}

	@Test
	public void successfullyEncodesMessage() throws Exception {
		TestMessage message = new TestMessage()
			.setRequiredLong(42L)
			.setRequiredString("Foo");

		ByteBuffer buffer = messageEncoder.encode(message);
		String json = StandardCharsets.UTF_8.decode(buffer).toString();

		//language=JSON
		String expectedMessageString = "{" +
			"\"requiredString\":\"Foo\"," +
			"\"requiredLong\":42," +
			"\"type\":\"TestMessage\"" +
			"}";

		assertThat(json).isEqualTo(expectedMessageString);
	}

	@Test(expected = ValidationException.class)
	public void rejectsInvalidMessages() throws Exception {
		TestMessage message = new TestMessage()
			.setRequiredLong(null)
			.setRequiredString(null);

		messageEncoder.encode(message);
	}

	@Test
	public void canEncodeLocaleDateTime() throws Exception {
		TestMessage message = new TestMessage()
			.setRequiredLong(42L)
			.setRequiredString("Foo")
			.setLocalDateTime(LocalDateTime.of(2020, 3, 12, 10, 25, 33, 534000000))
			.setLocalDate(LocalDate.of(2021, 12, 30));

		ByteBuffer buffer = messageEncoder.encode(message);
		String json = StandardCharsets.UTF_8.decode(buffer).toString();

		//language=JSON
		String expectedMessageString = "{" +
			"\"requiredString\":\"Foo\"," +
			"\"requiredLong\":42," +
			"\"localDateTime\":\"2020-03-12T10:25:33.534\"," +
			"\"localDate\":\"2021-12-30\"," +
			"\"type\":\"TestMessage\"" +
			"}";

		assertThat(json).isEqualTo(expectedMessageString);
	}

	@Test
	public void canEncodeInetAddress() throws Exception {
		TestMessage message = new TestMessage()
			.setRequiredLong(42L)
			.setRequiredString("Foo")
			.setInetAddressV4(InetAddress.getByName("10.73.0.42"))
			.setInetAddressV6(InetAddress.getByName("2a02:810b:c1c0:421f:8b9:baa:485d:b739"));

		ByteBuffer buffer = messageEncoder.encode(message);
		String json = StandardCharsets.UTF_8.decode(buffer).toString();

		//language=JSON
		String expectedMessageString = "{" +
			"\"requiredString\":\"Foo\"," +
			"\"requiredLong\":42," +
			"\"inetAddressV4\":\"10.73.0.42\"," +
			"\"inetAddressV6\":\"2a02:810b:c1c0:421f:8b9:baa:485d:b739\"," +
			"\"type\":\"TestMessage\"" +
			"}";

		assertThat(json).isEqualTo(expectedMessageString);
	}

	@Test
	public void canEncodeInetSocketAddress() throws Exception {
		TestMessage message = new TestMessage()
			.setRequiredLong(42L)
			.setRequiredString("Foo")
			.setSocketAddressV4(new InetSocketAddress("10.73.0.42", 9999))
			.setSocketAddressV6(new InetSocketAddress("2a02:810b:c1c0:421f:8b9:baa:485d:b739", 9999));

		ByteBuffer buffer = messageEncoder.encode(message);
		String json = StandardCharsets.UTF_8.decode(buffer).toString();

		//language=JSON
		String expectedMessageString = "{" +
			"\"requiredString\":\"Foo\"," +
			"\"requiredLong\":42," +
			"\"socketAddressV4\":\"10.73.0.42:9999\"," +
			"\"socketAddressV6\":\"[2a02:810b:c1c0:421f:8b9:baa:485d:b739]:9999\"," +
			"\"type\":\"TestMessage\"" +
			"}";

		//language=JSON
		assertThat(json).isEqualTo(expectedMessageString);
	}

	@Data
	@Accessors(chain = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private static class TestMessage {
		@NotEmpty
		private String requiredString;

		@NotNull
		private Long requiredLong;

		private LocalDateTime localDateTime;
		private LocalDate localDate;

		private InetAddress inetAddressV4;
		private InetAddress inetAddressV6;

		private InetSocketAddress socketAddressV4;
		private InetSocketAddress socketAddressV6;
	}
}
