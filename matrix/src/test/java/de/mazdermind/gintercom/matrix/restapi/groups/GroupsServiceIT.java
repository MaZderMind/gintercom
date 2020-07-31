package de.mazdermind.gintercom.matrix.restapi.groups;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.matrix.IntegrationTestBase;
import de.mazdermind.gintercom.matrix.configuration.model.GroupConfig;
import de.mazdermind.gintercom.matrix.tools.mocks.TestConfig;
import de.mazdermind.gintercom.matrix.tools.mocks.TestConfigDirectoryService;
import de.mazdermind.gintercom.mixingcore.MixingCore;

public class GroupsServiceIT extends IntegrationTestBase {
	@Autowired
	private GroupsService groupsService;

	@Autowired
	private TestConfig testConfig;

	@Autowired
	private MixingCore mixingCore;

	@Autowired
	private TestConfigDirectoryService testConfigDirectoryService;

	@Test
	public void getConfiguredGroups() {
		assertThat(groupsService.getConfiguredGroups())
			.hasSize(testConfig.getGroups().size())
			.extracting(GroupDto::getId)
			.containsAll(testConfig.getGroups().keySet());
	}

	@Test
	public void addGroup() {
		groupsService.addGroup(new GroupDto()
			.setId("ops")
			.setDisplay("Operations"));

		assertThat(testConfig.getGroups()).containsKey("ops");
		assertThat(mixingCore.getGroupById("ops")).isNotNull();
		assertThat(testConfigDirectoryService.getConfigDirectory().resolve("groups/ops.toml"))
			.exists().isRegularFile().hasContent("display = \"Operations\"");
	}

	@Test(expected = GroupAlreadyExistsException.class)
	public void addExistingGroup() {
		groupsService.addGroup(new GroupDto()
			.setId("ops")
			.setDisplay("Operations"));

		groupsService.addGroup(new GroupDto()
			.setId("ops")
			.setDisplay("More Operations"));
	}

	@Test
	public void getGroup() {
		String groupId = testConfig.addRandomGroup();
		GroupConfig groupConfig = testConfig.getGroups().get(groupId);

		assertThat(groupsService.getGroup(groupId)).isPresent().get()
			.returns(groupId, GroupDto::getId)
			.returns(groupConfig.getDisplay(), GroupDto::getDisplay);
	}

	@Test
	public void getNonExistingGroup() {
		assertThat(groupsService.getGroup("blafoo")).isNotPresent();
	}
}
