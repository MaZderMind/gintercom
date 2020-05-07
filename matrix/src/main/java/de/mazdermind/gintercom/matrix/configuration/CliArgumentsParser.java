package de.mazdermind.gintercom.matrix.configuration;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CliArgumentsParser {
	private final ApplicationArguments arguments;

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
			log.info("Parsing Command-Line Arguments");
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
