package de.mazdermind.gintercom.controlserver.shared;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import de.mazdermind.gintercom.clientapi.controlserver.shared.MalformedMessageException;
import de.mazdermind.gintercom.clientapi.controlserver.shared.MessageDecoder;

public class MessageDecoderTest {
	private static final String TEST_MESSAGE_PACKAGE = "de.mazdermind.gintercom.controlserver.shared";

	private MessageDecoder messageDecoder;

	@Before
	public void before() {
		ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		messageDecoder = new MessageDecoder(objectMapper, validator);
	}

	@Test
	public void successfullyDecodesMessage() throws Exception {
		//language=JSON
		String messageString = "{\n" +
			"\t\"type\": \"TestMessage\",\n" +
			"\t\"requiredString\": \"String-Value\",\n" +
			"\t\"requiredLong\": 42\n" +
			"}";
		ByteBuffer buffer = StandardCharsets.UTF_8.encode(messageString);
		TestMessage message = (TestMessage) messageDecoder.decode(buffer, TEST_MESSAGE_PACKAGE);

		assertThat(message).isNotNull();
		assertThat(message).isInstanceOf(TestMessage.class);
		assertThat(message.getRequiredString()).isEqualTo("String-Value");
		assertThat(message.getRequiredLong()).isEqualTo(42);
	}

	@Test(expected = MalformedInputException.class)
	public void rejectsInvalidUTF8() throws Exception {
		ByteBuffer buffer = ByteBuffer.wrap(new byte[]{ (byte) 0xC3, 0x28 });
		messageDecoder.decode(buffer, TEST_MESSAGE_PACKAGE);
	}

	@Test(expected = JsonParseException.class)
	public void rejectsInvalidJson() throws Exception {
		String messageString = "Foo";
		ByteBuffer buffer = StandardCharsets.UTF_8.encode(messageString);
		messageDecoder.decode(buffer, TEST_MESSAGE_PACKAGE);
	}

	@Test(expected = MalformedMessageException.class)
	public void rejectsEmptyMessage() throws Exception {
		String messageString = "";
		ByteBuffer buffer = StandardCharsets.UTF_8.encode(messageString);
		messageDecoder.decode(buffer, TEST_MESSAGE_PACKAGE);
	}

	@Test(expected = MalformedMessageException.class)
	public void rejectsJsonWithoutType() throws Exception {
		//language=JSON
		String messageString = "{\"foo\": \"bar\"}";
		ByteBuffer buffer = StandardCharsets.UTF_8.encode(messageString);
		messageDecoder.decode(buffer, TEST_MESSAGE_PACKAGE);
	}

	@Test(expected = MalformedMessageException.class)
	public void rejectsNotAllowedClasses() throws Exception {
		//language=JSON
		String messageString = "{\n" +
			"\t\"type\": \"SecurityContext\",\n" +
			"\t\"allow\": \"all\"\n" +
			"}";
		ByteBuffer buffer = StandardCharsets.UTF_8.encode(messageString);
		messageDecoder.decode(buffer, TEST_MESSAGE_PACKAGE);
	}

	@Test(expected = UnrecognizedPropertyException.class)
	public void rejectsAdditionalFields() throws Exception {
		//language=JSON
		String messageString = "{\n" +
			"\t\"type\": \"TestMessage\",\n" +
			"\t\"additionalField\": \"String-Value\"\n" +
			"}";
		ByteBuffer buffer = StandardCharsets.UTF_8.encode(messageString);
		messageDecoder.decode(buffer, TEST_MESSAGE_PACKAGE);
	}

	@Test(expected = ConstraintViolationException.class)
	public void rejectsMissingRequiredFields() throws Exception {
		//language=JSON
		String messageString = "{\n" +
			"\t\"type\": \"TestMessage\"\n" +
			"}";
		ByteBuffer buffer = StandardCharsets.UTF_8.encode(messageString);
		messageDecoder.decode(buffer, TEST_MESSAGE_PACKAGE);
	}

	@Test
	public void canDecodeLocaleDateTime() throws Exception {
		//language=JSON
		String messageString = "{\n" +
			"\t\"type\": \"TestMessage\",\n" +
			"\t\"requiredString\": \"String-Value\",\n" +
			"\t\"requiredLong\": 42,\n" +
			"\t\"localDateTime\": \"2020-03-12T10:25:33.534\",\n" +
			"\t\"localDate\": \"2021-12-30\"\n" +
			"}";
		ByteBuffer buffer = StandardCharsets.UTF_8.encode(messageString);
		TestMessage message = (TestMessage) messageDecoder.decode(buffer, TEST_MESSAGE_PACKAGE);

		assertThat(message.getLocalDateTime()).isEqualTo(LocalDateTime.of(2020, 3, 12, 10, 25, 33, 534000000));
		assertThat(message.getLocalDate()).isEqualTo(LocalDate.of(2021, 12, 30));
	}

	@Test
	public void canDecodeInetAddress() throws Exception {
		//language=JSON
		String messageString = "{\n" +
			"\t\"type\": \"TestMessage\",\n" +
			"\t\"requiredString\": \"String-Value\",\n" +
			"\t\"requiredLong\": 42,\n" +
			"\t\"inetAddressV4\": \"10.73.0.42\",\n" +
			"\t\"inetAddressV6\": \"2a02:810b:c1c0:421f:8b9:baa:485d:b739\"\n" +
			"}";
		ByteBuffer buffer = StandardCharsets.UTF_8.encode(messageString);
		TestMessage message = (TestMessage) messageDecoder.decode(buffer, TEST_MESSAGE_PACKAGE);

		assertThat(message.getInetAddressV4()).isEqualTo(InetAddress.getByName("10.73.0.42"));
		assertThat(message.getInetAddressV6()).isEqualTo(InetAddress.getByName("2a02:810b:c1c0:421f:8b9:baa:485d:b739"));
	}

	@Test
	public void canDecodeInetSocketAddress() throws Exception {
		//language=JSON
		String messageString = "{\n" +
			"\t\"type\": \"TestMessage\",\n" +
			"\t\"requiredString\": \"String-Value\",\n" +
			"\t\"requiredLong\": 42,\n" +
			"\t\"socketAddressV4\": \"10.73.0.42:9999\",\n" +
			"\t\"socketAddressV6\": \"[2a02:810b:c1c0:421f:8b9:baa:485d:b739]:9999\"\n" +
			"}";
		ByteBuffer buffer = StandardCharsets.UTF_8.encode(messageString);
		TestMessage message = (TestMessage) messageDecoder.decode(buffer, TEST_MESSAGE_PACKAGE);

		assertThat(message.getSocketAddressV4()).isEqualTo(
			new InetSocketAddress("10.73.0.42", 9999));
		assertThat(message.getSocketAddressV6()).isEqualTo(
			new InetSocketAddress("2a02:810b:c1c0:421f:8b9:baa:485d:b739", 9999));
	}

}
