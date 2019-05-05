package de.mazdermind.gintercom.matrix.integration;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import de.mazdermind.gintercom.matrix.MatrixApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MatrixApplication.class)
@ActiveProfiles({ "test" })
public abstract class IntegrationTestBase {
}
