package de.mazdermind.gintercom.matrix.tools.mocks;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Primary;

import de.mazdermind.gintercom.matrix.configuration.CliArguments;

@TestComponent
@Primary
public class TestCliArguments extends CliArguments {

	public TestCliArguments() {
		reset();
	}

	public void reset() {
		setConfigDirectory("/etc/gintercom");
	}
}
