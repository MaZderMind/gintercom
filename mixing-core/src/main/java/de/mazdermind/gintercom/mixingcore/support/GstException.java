package de.mazdermind.gintercom.mixingcore.support;

public class GstException extends RuntimeException {
	public GstException() {
	}

	public GstException(String message) {
		super(message);
	}

	public GstException(String message, Throwable cause) {
		super(message, cause);
	}

	public GstException(Throwable cause) {
		super(cause);
	}
}
