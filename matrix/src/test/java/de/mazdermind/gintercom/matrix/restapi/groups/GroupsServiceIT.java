package de.mazdermind.gintercom.matrix.restapi.groups;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.matrix.integration.IntegrationTestBase;
import de.mazdermind.gintercom.matrix.integration.TestConfig;

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
