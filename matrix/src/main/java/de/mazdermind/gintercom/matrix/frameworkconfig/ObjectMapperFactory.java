package de.mazdermind.gintercom.matrix.frameworkconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class ObjectMapperFactory {
	@Bean
	public ObjectMapper constructObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules();
		objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

		return objectMapper;
	}
}
