package de.mazdermind.gintercom.matrix.integration;

import org.freedesktop.gstreamer.Gst;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import de.mazdermind.gintercom.matrix.MatrixApplication;

@RunWith(SpringRunner.class)
@ContextConfiguration
@SpringBootTest(classes = MatrixApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ "IT" })
public abstract class IntegrationTestBase {
	@Before
	public void initGStreamer() {
		Gst.init();
	}
}
