package de.mazdermind.gintercom.matrix.integration;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import de.mazdermind.gintercom.matrix.configuration.CliArguments;

@Service
@Primary
public class IntegrationTestCliArguments implements CliArguments {
	@Override
	public String getConfigDirectory() {
		return IntegrationTestCliArguments.class.getClassLoader().getResource("integration/testconfig").getPath();
	}
}
