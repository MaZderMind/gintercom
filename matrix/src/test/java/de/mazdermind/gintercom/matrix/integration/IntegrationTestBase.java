package de.mazdermind.gintercom.matrix.integration;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import de.mazdermind.gintercom.matrix.MatrixApplication;

@RunWith(SpringRunner.class)
@ContextConfiguration
@SpringBootTest(classes = MatrixApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ "IT" })
public abstract class IntegrationTestBase {

	@LocalServerPort
	private int serverPort;

	public int getServerPort() {
		return serverPort;
	}

}
