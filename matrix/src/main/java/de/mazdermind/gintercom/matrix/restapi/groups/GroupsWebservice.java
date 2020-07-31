package de.mazdermind.gintercom.matrix.restapi.groups;

import java.util.stream.Stream;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	private void addGroup(@RequestBody @Valid GroupDto group) {
		groupsService.addGroup(group);
	}

	@GetMapping("/{id}")
	private GroupDto getGroup(@PathVariable @NotNull String id) {
		return groupsService.getGroup(id).orElse(null);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	private void deleteGroup(@PathVariable @NotNull String id) {
		groupsService.deleteGroup(id);
	}

}
