package de.mazdermind.gintercom.matrix.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.io.FilenameUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moandjiezana.toml.Toml;

import de.mazdermind.gintercom.matrix.configuration.model.ButtonSetConfig;
import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.configuration.model.GroupConfig;
import de.mazdermind.gintercom.matrix.configuration.model.MatrixConfig;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.configuration.evaluation.ReferenceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigLoaderService {
	private final ObjectMapper objectMapper;
	private final Toml toml;

	private final ConfigDirectoryService configDirectoryService;
	private final Validator validator;

	@Bean
	@ConditionalOnMissingBean(Config.class)
	public Config loadConfig() throws IOException {
		Path configDirectory = configDirectoryService.getConfigDirectory();
		log.info("Loading Configuration from Directory {}", configDirectory);

		Config config = new Config()
			.setMatrixConfig(loadConfigFile(configDirectory.resolve("matrix.toml"), MatrixConfig.class))
			.setPanels(loadConfigFiles(configDirectory.resolve("panels"), PanelConfig.class))
			.setGroups(loadConfigFiles(configDirectory.resolve("groups"), GroupConfig.class))
			.setButtonSets(loadConfigFiles(configDirectory.resolve("button-sets"), ButtonSetConfig.class));

		log.info("Validating Config");
		Set<ConstraintViolation<Config>> constraintViolations = validator.validate(config);

		if (constraintViolations.size() > 0) {
			log.error("Error validating config");
			for (ConstraintViolation<Config> constraintViolation : constraintViolations) {
				log.error("   {}: {}", constraintViolation.getPropertyPath(), constraintViolation.getMessage());
			}
			System.exit(1);
		}

		ReferenceValidator.validateReferences(config);

		log.info("Loaded {} Panels, {} Groups and {} ButtonSets from Config",
			config.getPanels().size(),
			config.getGroups().size(),
			config.getButtonSets().size());

		return config;
	}

	private <T> T loadConfigFile(Path configFile, Class<T> klazz) throws IOException {
		InputStream inputStream = Files.newInputStream(configFile, StandardOpenOption.READ);
		Map<String, Object> map = toml.read(inputStream).toMap();
		try {
			return objectMapper.convertValue(map, klazz);
		} catch (IllegalArgumentException e) {
			log.error("Error parsing Configuration-File {} as {}: {}", configFile, klazz.getSimpleName(), e.getMessage());
			System.exit(1);
			throw e;
		}
	}

	private <T> Map<String, T> loadConfigFiles(Path directory, Class<T> klazz) throws IOException {
		return Files.list(directory).collect(Collectors.toMap(
			path -> FilenameUtils.getBaseName(path.getFileName().toString()),
			path -> {
				try {
					return loadConfigFile(path, klazz);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		));
	}
}
