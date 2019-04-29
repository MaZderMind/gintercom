package de.mazdermind.gintercom.debugclient;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TestComponent {
	private static Logger log = LoggerFactory.getLogger(TestComponent.class);

	@PostConstruct
	public void postConstruct() {
		log.info("postConstruct");
	}

	@PreDestroy
	public void preDestroy() {
		log.info("preDestroy");
	}
}
