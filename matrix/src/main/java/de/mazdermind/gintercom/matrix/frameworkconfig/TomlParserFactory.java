package de.mazdermind.gintercom.matrix.frameworkconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.moandjiezana.toml.Toml;

@Configuration
public class TomlParserFactory {
	@Bean
	public Toml constructTomlParser() {
		return new Toml();
	}
}
