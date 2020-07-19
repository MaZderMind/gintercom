package de.mazdermind.gintercom.matrix.restapi.groups;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.matrix.IntegrationTestBase;
import de.mazdermind.gintercom.matrix.tools.mocks.TestConfig;

public class GroupsServiceIT extends IntegrationTestBase {
	@Autowired
	private GroupsService groupsService;

	@Autowired
	private TestConfig testConfig;

	@Test
	public void getConfiguredGroups() {
		Assertions.assertThat(groupsService.getConfiguredGroups())
			.hasSize(testConfig.getGroups().size())
			.extracting(GroupDto::getId).containsAll(testConfig.getGroups().keySet());
	}
}
