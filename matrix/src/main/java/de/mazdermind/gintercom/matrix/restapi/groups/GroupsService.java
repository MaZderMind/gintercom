package de.mazdermind.gintercom.matrix.restapi.groups;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import de.mazdermind.gintercom.matrix.configuration.ConfigWriterService;
import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.configuration.model.GroupConfig;
import de.mazdermind.gintercom.mixingcore.MixingCore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupsService {
	private final Config config;
	private final ConfigWriterService configWriterService;
	private final MixingCore mixingCore;

	public Stream<GroupDto> getConfiguredGroups() {
		return config.getGroups().entrySet().stream()
			.sorted(Map.Entry.comparingByKey())
			.map(entry -> new GroupDto(entry.getKey(), entry.getValue()));
	}

	public void addGroup(GroupDto groupDto) {
		log.info("Adding Group {} to Config", groupDto.getId());
		config.getGroups().put(groupDto.getId(), new GroupConfig()
			.setDisplay(groupDto.getDisplay()));

		configWriterService.writeConfig();

		log.info("Adding Group {} to MixingCore", groupDto.getId());
		mixingCore.addGroup(groupDto.getId());
	}

	public Optional<GroupDto> getGroup(String groupId) {
		return Optional.ofNullable(config.getGroups().get(groupId))
			.map(groupConfig -> new GroupDto(groupId, groupConfig));
	}

	public Optional<GroupDto> deleteGroup(String groupId) {
		log.info("Removing Group {} from Config", groupId);
		GroupConfig removedGroupConfig = config.getGroups().remove(groupId);

		log.info("Removing Group {} from MixingCore", groupId);
		Optional.ofNullable(mixingCore.getGroupById(groupId))
			.ifPresent(mixingCore::removeGroup);

		return Optional.ofNullable(removedGroupConfig)
			.map(groupConfig -> new GroupDto(groupId, groupConfig));
	}
}
