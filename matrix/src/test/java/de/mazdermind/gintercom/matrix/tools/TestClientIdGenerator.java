package de.mazdermind.gintercom.matrix.tools;

import org.apache.commons.lang3.RandomStringUtils;

public class TestClientIdGenerator {
	public static String generateTestClientId() {
		return String.format("TEST-%s",
			RandomStringUtils.randomNumeric(4).toUpperCase()
		);
	}
}
