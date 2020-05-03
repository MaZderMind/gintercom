package de.mazdermind.gintercom.matrix.mixingcore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.mixingcore.MixingCore;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class MixingCoreBean {
	private final Config config;

	public MixingCoreBean(
		@Autowired Config config
	) {
		this.config = config;
	}

	@Bean
	public MixingCore createMixingCoreBean() {
		log.info("Creating new MixingCore Bean");
		MixingCore mixingCore = new MixingCore();
		configureInitialGroups(mixingCore);
		return mixingCore;
	}

	private void configureInitialGroups(MixingCore mixingCore) {
		log.info("Configuring initial Groups");
		config.getGroups().forEach((name, config) -> mixingCore.addGroup(name));
	}
}
