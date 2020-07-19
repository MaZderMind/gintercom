package de.mazdermind.gintercom.debugclient;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("de.mazdermind.gintercom")
@EnableScheduling
public class DebugClientApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(DebugClientApplication.class)
			.web(WebApplicationType.NONE)
			.headless(false)
			.run(args);
	}

}
