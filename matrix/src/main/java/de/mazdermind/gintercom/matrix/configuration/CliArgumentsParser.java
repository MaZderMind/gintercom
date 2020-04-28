package de.mazdermind.gintercom.matrix.configuration;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class CliArgumentsParser {
	private static final Logger log = LoggerFactory.getLogger(CliArgumentsParser.class);
	private final ApplicationArguments arguments;

	public CliArgumentsParser(
		@Autowired ApplicationArguments arguments
	) {
		this.arguments = arguments;
	}

	@Bean
	@ConditionalOnMissingBean(CliArguments.class)
	public CliArguments getCliArgs() throws ParseException {
		Options options = new Options();

		Option input = new Option("c", "config-directory", true, "Path of the Directory containing the Configuration-Files");
		input.setRequired(true);
		options.addOption(input);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();

		try {
			log.info("Parsing Comand-Line Arguments");
			CommandLine commandLine = parser.parse(options, arguments.getSourceArgs());
			return new CliArguments()
				.setConfigDirectory(commandLine.getOptionValue("config-directory"));

		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("java -jar matrix.jar", options);

			System.exit(1);
			throw e;
		}
	}
}