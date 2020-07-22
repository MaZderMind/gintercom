package de.mazdermind.gintercom.clientsupport.clientid;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomClientIdGenerator {
	public static String generateRandomClientId() {
		return String.format("%s-%s",
			RandomStringUtils.randomNumeric(4).toUpperCase(),
			RandomStringUtils.randomNumeric(4).toUpperCase()
		);
	}
}
