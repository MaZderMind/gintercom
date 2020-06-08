package de.mazdermind.gintercom.clientapi.controlserver.shared;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
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
public class MessageEncoder {
	private final ObjectMapper objectMapper;
	private final Validator validator;

	private final CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder()
			.onMalformedInput(CodingErrorAction.REPORT)
			.onUnmappableCharacter(CodingErrorAction.REPORT);

	public ByteBuffer encode(Object message) throws Exception {
		Set<ConstraintViolation<Object>> constraintViolations = validator.validate(message);
		if (!constraintViolations.isEmpty()) {
			throw new ConstraintViolationException(constraintViolations);
		}

		JsonNode json = objectMapper.valueToTree(message);
		((ObjectNode) json).put("type", message.getClass().getSimpleName());

		String jsonString = json.toString() + "\n";
		return encoder.encode(CharBuffer.wrap(jsonString));
	}
}
