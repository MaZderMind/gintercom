package de.mazdermind.gintercom.matrix.configuration;

import org.springframework.stereotype.Service;

import de.mazdermind.gintercom.matrix.configuration.model.Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConfigWriterService {
	private final Config config;

	public void writeConfig() {
		log.info("Writing config to files: {}", config);
	}
}
