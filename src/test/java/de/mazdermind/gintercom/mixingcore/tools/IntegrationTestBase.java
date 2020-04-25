package de.mazdermind.gintercom.mixingcore.tools;

import org.junit.After;
import org.junit.Before;

import de.mazdermind.gintercom.mixingcore.tools.MixingCoreTestManager;

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
