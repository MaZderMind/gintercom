package de.mazdermind.gintercom.matrix;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import de.mazdermind.gintercom.matrix.tools.mocks.TestConfig;
import de.mazdermind.gintercom.matrix.tools.mocks.TestConfigDirectoryService;
import de.mazdermind.gintercom.mixingcore.MixingCore;

@RunWith(SpringRunner.class)
@ContextConfiguration
@SpringBootTest(classes = MatrixApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ "IT" })
public abstract class IntegrationTestBase {
	@Autowired
	private TestConfig testConfig;

	@Autowired
	private MixingCore mixingCore;

	@Autowired
	private TestConfigDirectoryService testConfigDirectoryService;

	@Before
	public void resetConfig() {
		testConfig.reset();
	}

	@Before
	public void resetMixingCore() {
		mixingCore.clear();
	}

	@Before
	public void resetConfigDirectory() {
		testConfigDirectoryService.reset();
	}
}
