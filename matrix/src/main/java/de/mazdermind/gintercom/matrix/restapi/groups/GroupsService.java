package de.mazdermind.gintercom.matrix.restapi.groups;

import java.util.Map;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import de.mazdermind.gintercom.matrix.configuration.model.Config;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupsService {
	private final Config config;

	public Stream<GroupDto> getConfiguredGroups() {
		return config.getGroups().entrySet().stream()
			.sorted(Map.Entry.comparingByKey())
			.map(entry -> new GroupDto(entry.getKey(), entry.getValue()));
	}
}
