package de.mazdermind.gintercom.matrix.configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConfigDirectoryService {
	private final CliArguments cliArguments;

	public Path getConfigDirectory() {
		return Paths.get(cliArguments.getConfigDirectory());
	}
}
