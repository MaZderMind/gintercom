package de.mazdermind.gintercom.mixingcore.exception;

public class InvalidOperationException extends RuntimeException {
	public InvalidOperationException() {
	}

	public InvalidOperationException(String message) {
		super(message);
	}

	public InvalidOperationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidOperationException(Throwable cause) {
		super(cause);
	}

	public InvalidOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
