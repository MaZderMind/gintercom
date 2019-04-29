package de.mazdermind.gintercom.debugclient;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("de.mazdermind.gintercom")
public class DebugClientApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(DebugClientApplication.class)
			.headless(false)
			.run(args);
	}

}
