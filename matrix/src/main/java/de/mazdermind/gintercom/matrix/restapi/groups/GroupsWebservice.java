package de.mazdermind.gintercom.matrix.restapi.groups;

import java.util.stream.Stream;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.mazdermind.gintercom.matrix.restapi.UsageDto;
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
	private void addGroup(@RequestBody @Valid GroupDto group) {
		groupsService.addGroup(group);
	}

	@PutMapping
	private void updateGroup(@RequestBody @Valid GroupDto group) {
		groupsService.updateGroup(group);
	}

	@GetMapping("/{id}")
	private GroupDto getGroup(@PathVariable @NotNull String id) {
		return groupsService.getGroup(id).orElse(null);
	}

	@DeleteMapping("/{id}")
	private void deleteGroup(@PathVariable @NotNull String id) {
		groupsService.deleteGroup(id);
	}

	@GetMapping("/{id}/usage")
	private UsageDto getGroupUsage(@PathVariable @NotNull String id) {
		return groupsService.getGroupUsage(id);
	}

}
