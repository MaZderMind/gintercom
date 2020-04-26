package de.mazdermind.gintercom.mixingcore.it.tools;

import org.junit.After;
import org.junit.Before;

public abstract class IntegrationTestBase {
	protected MixingCoreTestManager testManager;

	@Before
	public void setupTestManager() {
		testManager = MixingCoreTestManager.getInstance();
	}

	@After
	public void cleanupTestManager() {
		testManager.cleanup();
	}
}
