package de.mazdermind.gintercom.matrix.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moandjiezana.toml.Toml;

import de.mazdermind.gintercom.matrix.configuration.model.ButtonSetConfig;
import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.configuration.model.GroupConfig;
import de.mazdermind.gintercom.matrix.configuration.model.MatrixConfig;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;

@Service
public class ConfigParser {
	private static final Logger log = LoggerFactory.getLogger(ConfigParser.class);

	private final ObjectMapper objectMapper;
	private final Toml toml;

	private final CliArguments cliArguments;
	private final Validator validator;

	public ConfigParser(
		@Autowired CliArguments cliArguments,
		@Autowired Validator validator
	) {
		this.cliArguments = cliArguments;
		this.validator = validator;

		objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules();
		objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

		toml = new Toml();
	}

	@Bean
	@ConditionalOnMissingBean(Config.class)
	public Config loadConfig() throws IOException {
		String configDirectory = cliArguments.getConfigDirectory();
		log.info("Loading Configuration from Directory {}", configDirectory);

		Config config = new Config()
			.setMatrixConfig(loadConfigFile(Paths.get(configDirectory, "matrix.toml"), MatrixConfig.class))
			.setPanels(loadConfigFiles(Paths.get(configDirectory, "panels"), PanelConfig.class))
			.setGroups(loadConfigFiles(Paths.get(configDirectory, "groups"), GroupConfig.class))
			.setButtonsets(loadConfigFiles(Paths.get(configDirectory, "buttonsets"), ButtonSetConfig.class));

		log.info("Validating Config");
		Set<ConstraintViolation<Config>> constraintViolations = validator.validate(config);

		if (constraintViolations.size() > 0) {
			log.error("Error validating config");
			for (ConstraintViolation<Config> constraintViolation : constraintViolations) {
				log.error("   {}: {}", constraintViolation.getPropertyPath(), constraintViolation.getMessage());
			}
			System.exit(1);
		}

		config.validateReferences();

		log.info("Loaded {} Panels, {} Groups and {} Buttonsets from Config",
			config.getPanels().size(),
			config.getGroups().size(),
			config.getButtonsets().size());

		return config;
	}

	private <T> T loadConfigFile(Path configFile, Class<T> klazz) throws IOException {
		InputStream inputStream = Files.newInputStream(configFile, StandardOpenOption.READ);
		Map<String, Object> readMap = toml.read(inputStream).toMap();
		try {
			return objectMapper.convertValue(readMap, klazz);
		} catch (IllegalArgumentException e) {
			log.error("Error parsing Configuration-File {} as {}: {}", configFile, klazz.getSimpleName(), e.getMessage());
			System.exit(1);
			throw e;
		}
	}

	private <T> Map<String, T> loadConfigFiles(Path folder, Class<T> klazz) throws IOException {
		return Files.list(folder).collect(Collectors.toMap(
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
