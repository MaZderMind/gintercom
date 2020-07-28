package de.mazdermind.gintercom.matrix.tools;

import org.apache.commons.lang3.RandomStringUtils;

public class TestClientIdGenerator {
	public static String generateTestClientId() {
		return String.format("TEST-%s-%s",
			RandomStringUtils.randomAlphanumeric(4).toUpperCase(),
			RandomStringUtils.randomAlphanumeric(4).toUpperCase()
		);
	}
}
