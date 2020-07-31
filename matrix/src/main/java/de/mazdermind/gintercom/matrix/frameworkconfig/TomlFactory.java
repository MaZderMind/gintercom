package de.mazdermind.gintercom.matrix.frameworkconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

@Configuration
public class TomlFactory {
	@Bean
	public Toml constructTomlParser() {
		return new Toml();
	}

	@Bean
	public TomlWriter constructTomlWriter() {
		return new TomlWriter();
	}
}
