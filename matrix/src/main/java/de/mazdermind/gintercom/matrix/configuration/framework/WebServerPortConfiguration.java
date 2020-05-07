package de.mazdermind.gintercom.matrix.configuration.framework;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;

import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.configuration.model.ServerConfig;

@Configuration
public class WebServerPortConfiguration implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
	private final Config config;

	public WebServerPortConfiguration(Config config) {
		this.config = config;
	}

	@Override
	public void customize(ConfigurableServletWebServerFactory server) {
		ServerConfig webuiConfig = config.getMatrixConfig().getWebui();
		server.setAddress(webuiConfig.getBind());
		server.setPort(webuiConfig.getPort());
	}
}
