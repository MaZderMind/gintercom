package de.mazdermind.gintercom.matrix.configuration;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
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
		log.debug("Writing Config-File {}", configFile);
		try {
			Map<String, Object> map = objectMapper.convertValue(config, new TypeReference<Map<String, Object>>() {
			});
			OutputStream outputStream = Files.newOutputStream(configFile,
				StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
			tomlWriter.write(map, outputStream);
		} catch (IOException e) {
			throw new ConfigWriteException(e);
		}
	}

	private <T> void writeConfigFiles(Map<String, T> configs, Path directory) {
		try {
			Files.createDirectory(directory);
			log.info("Created Directory {}", directory);
		} catch (FileAlreadyExistsException e) {
			log.debug("Directory {} already exists", directory);
		} catch (IOException e) {
			throw new ConfigWriteException(e);
		}

		log.info("Writing Config-Files in {}", directory);
		configs.forEach((id, config) -> {
			Path configFile = directory.resolve(id + ".toml");
			writeConfigFile(config, configFile);
		});

		log.info("Deleting extra Config-Files from {}", directory);
		try {
			Files.list(directory)
				.filter(path -> {
					String objectId = FilenameUtils.getBaseName(path.getFileName().toString());
					return !configs.containsKey(objectId);
				})
				.forEach(path -> {
					try {
						Files.delete(path);
						log.debug("Deleted Config-File {}", path);
					} catch (IOException e) {
						log.warn("Could not delete Config-File {}", path, e);
					}
				});
		} catch (IOException e) {
			throw new ConfigWriteException(e);
		}
	}

	private static class ConfigWriteException extends RuntimeException {
		public ConfigWriteException(Exception cause) {
			super("Could not write Config", cause);
		}
	}
}
