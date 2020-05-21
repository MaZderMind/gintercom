package de.mazdermind.gintercom.matrix.restapi.groups;

import java.util.stream.Stream;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rest/groups")
@RequiredArgsConstructor
public class GroupsWebservice {
	private final GroupsService groupsService;

	@GetMapping
	private Stream<GroupDto> getConfiguredGroups() {
		return groupsService.getConfiguredGroups();
	}
}
