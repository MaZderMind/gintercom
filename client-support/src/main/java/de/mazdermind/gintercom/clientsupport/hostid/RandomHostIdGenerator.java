package de.mazdermind.gintercom.clientsupport.hostid;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomHostIdGenerator {
	public static String generateRandomHostId() {
		return String.format("%s-%s",
			RandomStringUtils.randomNumeric(4).toUpperCase(),
			RandomStringUtils.randomNumeric(4).toUpperCase()
		);
	}
}
