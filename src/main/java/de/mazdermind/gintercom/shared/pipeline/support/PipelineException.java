package de.mazdermind.gintercom.shared.pipeline.support;

public class PipelineException extends RuntimeException {
	public PipelineException() {
	}

	public PipelineException(String message) {
		super(message);
	}

	public PipelineException(String message, Throwable cause) {
		super(message, cause);
	}

	public PipelineException(Throwable cause) {
		super(cause);
	}
}
