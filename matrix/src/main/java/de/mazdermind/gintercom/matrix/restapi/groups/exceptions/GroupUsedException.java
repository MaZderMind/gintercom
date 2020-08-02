package de.mazdermind.gintercom.matrix.restapi.groups.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

public class GroupUsedException extends HttpStatusCodeException {
	public GroupUsedException(String groupId) {
		super(HttpStatus.CONFLICT, String.format("Group with Id %s is still in use", groupId));
	}

	public GroupUsedException(String groupId, int numUses) {
		super(HttpStatus.CONFLICT, String.format("Group with Id %s is still in use by %d users", groupId, numUses));
	}
}
