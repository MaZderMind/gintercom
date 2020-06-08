package de.mazdermind.gintercom.matrix;

import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import de.mazdermind.gintercom.matrix.tools.mocks.TestConfig;
import de.mazdermind.gintercom.mixingcore.MixingCore;

@RunWith(SpringRunner.class)
@ContextConfiguration
@SpringBootTest(classes = MatrixApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ "IT" })
public abstract class IntegrationTestBase {
	@MockBean
	private MixingCore mixingCore;

	@Autowired
	private TestConfig testConfig;

	protected MixingCore getMixingCoreMock() {
		return mixingCore;
	}

	@After
	public void resetConfig() {
		testConfig.reset();
	}
}
