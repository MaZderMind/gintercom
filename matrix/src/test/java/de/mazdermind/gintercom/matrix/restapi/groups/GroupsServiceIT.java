package de.mazdermind.gintercom.matrix.restapi.groups;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableMap;
import com.oblac.nomen.Nomen;

import de.mazdermind.gintercom.clientapi.configuration.ButtonConfig;
import de.mazdermind.gintercom.clientapi.configuration.CommunicationTargetType;
import de.mazdermind.gintercom.matrix.IntegrationTestBase;
import de.mazdermind.gintercom.matrix.configuration.model.ButtonSetConfig;
import de.mazdermind.gintercom.matrix.configuration.model.GroupConfig;
import de.mazdermind.gintercom.matrix.restapi.UsageDto;
import de.mazdermind.gintercom.matrix.restapi.groups.exceptions.GroupAlreadyExistsException;
import de.mazdermind.gintercom.matrix.restapi.groups.exceptions.GroupNotFoundException;
import de.mazdermind.gintercom.matrix.restapi.groups.exceptions.GroupUsedException;
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

	private String groupId;

	@Before
	public void before() {
		groupId = Nomen.randomName();
	}

	@Test
	public void getConfiguredGroups() {
		testConfig.addRandomGroup();
		testConfig.addRandomGroup();
		testConfig.addRandomGroup();

		assertThat(groupsService.getConfiguredGroups())
			.hasSize(testConfig.getGroups().size())
			.extracting(GroupDto::getId)
			.containsAll(testConfig.getGroups().keySet());
	}

	@Test
	public void addGroup() {
		groupsService.addGroup(new GroupDto()
			.setId(groupId)
			.setDisplay("Operations"));

		assertThat(testConfig.getGroups()).containsKey(groupId);
		assertThat(testConfig.getGroups().get(groupId).getDisplay()).isEqualTo("Operations");
		assertThat(mixingCore.getGroupById(groupId)).isNotNull();
		assertThat(testConfigDirectoryService.getConfigDirectory().resolve("groups/" + groupId + ".toml"))
			.exists().isRegularFile().hasContent("display = \"Operations\"");
	}

	@Test
	public void updateGroup() {
		groupsService.addGroup(new GroupDto()
			.setId(groupId)
			.setDisplay("Operations"));

		groupsService.updateGroup(new GroupDto()
			.setId(groupId)
			.setDisplay("Ops"));

		assertThat(testConfig.getGroups()).containsKey(groupId);
		assertThat(testConfig.getGroups().get(groupId).getDisplay()).isEqualTo("Ops");
		assertThat(mixingCore.getGroupById(groupId)).isNotNull();
		assertThat(testConfigDirectoryService.getConfigDirectory().resolve("groups/" + groupId + ".toml"))
			.exists().isRegularFile().hasContent("display = \"Ops\"");

		groupsService.updateGroup(new GroupDto()
			.setId(groupId)
			.setDisplay(""));

		assertThat(testConfig.getGroups().get(groupId).getDisplay()).isEqualTo("");
		assertThat(testConfigDirectoryService.getConfigDirectory().resolve("groups/" + groupId + ".toml"))
			.exists().isRegularFile().hasContent("display = \"\"");
	}

	@Test(expected = GroupAlreadyExistsException.class)
	public void addExistingGroupFails() {
		groupsService.addGroup(new GroupDto().setId(groupId));
		groupsService.addGroup(new GroupDto().setId(groupId));
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
		assertThat(groupsService.getGroup(groupId)).isNotPresent();
	}

	@Test
	public void deleteGroup() {
		groupsService.addGroup(new GroupDto().setId(groupId));

		assertThat(testConfig.getGroups()).containsKey(groupId);
		assertThat(mixingCore.getGroupById(groupId)).isNotNull();
		assertThat(testConfigDirectoryService.getConfigDirectory().resolve("groups/" + groupId + ".toml"))
			.isRegularFile();

		groupsService.deleteGroup(groupId);

		assertThat(testConfig.getGroups()).doesNotContainKey(groupId);
		assertThat(mixingCore.getGroupById(groupId)).isNull();
		assertThat(testConfigDirectoryService.getConfigDirectory().resolve("groups/" + groupId + ".toml"))
			.doesNotExist();
	}

	@Test(expected = GroupNotFoundException.class)
	public void deleteNonExistingGroupFails() {
		groupsService.deleteGroup(groupId);
	}

	@Test
	public void deleteGroupDoesNotFailWhenConfigFileIsMissing() throws IOException {
		groupsService.addGroup(new GroupDto().setId(groupId));

		Files.delete(
			testConfigDirectoryService.getConfigDirectory().resolve("groups/" + groupId + ".toml")
		);

		groupsService.deleteGroup(groupId);
	}

	@Test(expected = GroupUsedException.class)
	public void deleteUsedGroupFails() {
		groupsService.addGroup(new GroupDto().setId(groupId));

		String panelId = testConfig.addRandomPanel();
		testConfig.getPanels().get(panelId)
			.getRxGroups().add(groupId);

		groupsService.deleteGroup(groupId);
	}


	@Test(expected = GroupNotFoundException.class)
	public void getUsageForNonExistingGroupFails() {
		groupsService.getGroupUsage(groupId);
	}

	@Test
	public void getUsageReturnsUnusedForUnusedGroup() {
		groupsService.addGroup(new GroupDto().setId(groupId));

		UsageDto groupUsage = groupsService.getGroupUsage(groupId);
		assertThat(groupUsage.isUsed()).isFalse();
		assertThat(groupUsage.getUsers()).isEmpty();
	}

	@Test
	public void getUsageReturnsUsersOfUsedGroup() {
		groupsService.addGroup(new GroupDto().setId(groupId));

		String panelId = testConfig.addRandomPanel();
		testConfig.getPanels().get(panelId)
			.getRxGroups().add(groupId);

		String buttonSetId = Nomen.randomName();
		testConfig.getButtonSets().put(buttonSetId, new ButtonSetConfig()
			.setButtons(ImmutableMap.of(
				"q1", new ButtonConfig()
					.setTargetType(CommunicationTargetType.GROUP)
					.setTarget(groupId)
			)));

		UsageDto groupUsage = groupsService.getGroupUsage(groupId);
		assertThat(groupUsage.isUsed()).isTrue();
		assertThat(groupUsage.getUsers())
			.containsOnly("Group " + groupId + " is used as rxGroup of Panel " + panelId,
				"Group " + groupId + " is used as target of Button q1 of ButtonSet " + buttonSetId);
	}
}
