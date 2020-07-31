package de.mazdermind.gintercom.matrix.restapi.groups;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class GroupNotFoundException extends ResponseStatusException {
	public GroupNotFoundException(String groupId) {
		super(HttpStatus.NOT_FOUND, String.format("No Group with Id '%s' found", groupId));
	}
}
