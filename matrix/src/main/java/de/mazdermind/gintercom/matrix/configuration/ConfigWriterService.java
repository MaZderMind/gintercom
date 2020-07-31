package de.mazdermind.gintercom.matrix.configuration;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moandjiezana.toml.TomlWriter;

import de.mazdermind.gintercom.matrix.configuration.model.Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConfigWriterService {
	private final ObjectMapper objectMapper;
	private final TomlWriter tomlWriter;

	private final ConfigDirectoryService configDirectoryService;
	private final Config config;

	public void writeConfig() {
		Path configDirectory = configDirectoryService.getConfigDirectory();
		log.info("Writing Configuration to Directory {}", configDirectory);

		writeConfigFile(config.getMatrixConfig(), configDirectory.resolve("matrix.toml"));
		writeConfigFiles(config.getPanels(), configDirectory.resolve("panels"));
		writeConfigFiles(config.getGroups(), configDirectory.resolve("groups"));
		writeConfigFiles(config.getButtonSets(), configDirectory.resolve("button-sets"));
	}

	private <T> void writeConfigFile(T config, Path configFile) {
		try {
			Map<String, Object> map = objectMapper.convertValue(config, new TypeReference<Map<String, Object>>() {
			});
			OutputStream outputStream = Files.newOutputStream(configFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
			tomlWriter.write(map, outputStream);
		} catch (IOException e) {
			throw new ConfigWriteException(e);
		}
	}

	private <T> void writeConfigFiles(Map<String, T> configs, Path folder) {
		try {
			Files.createDirectory(folder);
		} catch (FileAlreadyExistsException e) {
			// pass
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		configs.forEach((id, config) -> {
			Path configFile = folder.resolve(id + ".toml");
			writeConfigFile(config, configFile);
		});
	}

	private static class ConfigWriteException extends RuntimeException {
		public ConfigWriteException(Exception cause) {
			super("Could not write Config", cause);
		}
	}
}
