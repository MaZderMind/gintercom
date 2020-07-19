package de.mazdermind.gintercom.clientapi.controlserver.shared;

public class MalformedMessageException extends RuntimeException {
	public MalformedMessageException(String reason) {
		super(reason);
	}
}
