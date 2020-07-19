package de.mazdermind.gintercom.matrix.tools;

import org.apache.commons.lang3.RandomStringUtils;

public class TestHostIdGenerator {
	public static String generateTestHostId() {
		return String.format("TEST-%s",
			RandomStringUtils.randomNumeric(4).toUpperCase()
		);
	}
}
