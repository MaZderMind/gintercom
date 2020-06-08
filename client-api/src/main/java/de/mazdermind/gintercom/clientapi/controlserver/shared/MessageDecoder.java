package de.mazdermind.gintercom.clientapi.controlserver.shared;

import java.nio.ByteBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Converts Bytes to validated Message-Objects.
 * <p>
 * the ByteBuffer is decodes as UTF-8 Text, then parsed to JSON. The JSON is expected to contain a "type"-Field which is used to
 * select the correct Target-Class from the allowedMessages-List by the Classes SimpleName. The JSON-Message (without the already
 * consumed "type"-Field) is mapped to the correct Message-Class and the resulting Object-Instance is validated against the
 * Java-Bean-Validator. If the Validation succeeds, the successfully converted Message-Object is returned; in all other Cases an
 * Exception is thrown.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MessageDecoder {
	private final ObjectMapper objectMapper;
	private final Validator validator;

	private final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
		.onMalformedInput(CodingErrorAction.REPORT)
		.onUnmappableCharacter(CodingErrorAction.REPORT);

	/**
	 * Parses the Byte-Buffer as UTF-8 JSON, which is matched to a POJO Message-Class based on its "type" Attribute.
	 * If the specified Message-Class is in the List of allowed Messages, an Instance will be created from the JSON
	 * and validated according to Bean-Validation rules. If the Validation succeeds, the successfully converted Message-Object is returned; in all other Cases an
	 * Exception is thrown.
	 *
	 * @param buffer          Byte-Buffer to convert
	 * @param allowedMessages Map of allowed Message-Types
	 * @return Instance of the successfully converted Message
	 * @throws Exception One of multiple Exceptions can be thrown, when an invalid or not allowed Message is received
	 */
	public Object decode(ByteBuffer buffer, Map<String, Class<?>> allowedMessages) throws Exception {
		String string = decoder.decode(buffer).toString();
		JsonNode json = objectMapper.readTree(string);

		if (json == null) {
			throw new MalformedMessageException("No JSON found");
		}

		if (!json.has("type")) {
			throw new MalformedMessageException("type-field missing");
		}

		String messageType = json.get("type").textValue();
		if (!allowedMessages.containsKey(messageType)) {
			throw new MalformedMessageException(String.format("type-field does not name a valid Message (expected one of %s)",
				allowedMessages.keySet()));
		}

		((ObjectNode) json).remove("type");

		Class<?> messageTypeClass = allowedMessages.get(messageType);
		Object parsedMessage = objectMapper.treeToValue(json, messageTypeClass);

		Set<ConstraintViolation<Object>> constraintViolations = validator.validate(parsedMessage);
		if (!constraintViolations.isEmpty()) {
			throw new ConstraintViolationException(constraintViolations);
		}

		return parsedMessage;
	}
}
