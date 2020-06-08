package de.mazdermind.gintercom.matrix;

import org.junit.Before;
import org.springframework.boot.web.server.LocalServerPort;

import io.restassured.RestAssured;

public abstract class RestTestBase extends IntegrationTestBase {
	@LocalServerPort
	private int port;

	@Before
	public void configureRestAssured() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
	}
}
