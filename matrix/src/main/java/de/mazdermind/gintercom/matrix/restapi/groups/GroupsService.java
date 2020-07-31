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
		String groupId = groupDto.getId();
		if (config.getGroups().containsKey(groupId)) {
			throw new GroupAlreadyExistsException(groupId);
		}

		log.info("Adding Group {} to Config", groupId);
		config.getGroups().put(groupId, new GroupConfig()
			.setDisplay(groupDto.getDisplay()));

		configWriterService.writeConfig();

		log.info("Adding Group {} to MixingCore", groupId);
		mixingCore.addGroup(groupId);
	}

	public Optional<GroupDto> getGroup(String groupId) {
		return Optional.ofNullable(config.getGroups().get(groupId))
			.map(groupConfig -> new GroupDto(groupId, groupConfig));
	}

	public void deleteGroup(String groupId) {
		if (!config.getGroups().containsKey(groupId)) {
			throw new GroupAlreadyExistsException(groupId);
		}

		log.info("Removing Group {} from Config", groupId);
		config.getGroups().remove(groupId);

		log.info("Removing Group {} from MixingCore", groupId);
		mixingCore.removeGroup(mixingCore.getGroupById(groupId));
	}
}
