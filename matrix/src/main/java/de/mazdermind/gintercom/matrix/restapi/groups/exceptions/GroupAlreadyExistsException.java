package de.mazdermind.gintercom.matrix.restapi.groups.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class GroupAlreadyExistsException extends ResponseStatusException {
	public GroupAlreadyExistsException(String groupId) {
		super(HttpStatus.CONFLICT, String.format("Group with Id '%s' already exists", groupId));
	}
}
