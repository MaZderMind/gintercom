package de.mazdermind.gintercom.debugclient.configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

import com.google.common.base.Splitter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CliArguments {
	private final ApplicationArguments arguments;
	private CommandLine commandLine;

	@PostConstruct
	public void parseArgs() throws ParseException {
		Options options = new Options();

		options.addOption(new Option("H", "host", true,
			"Hostname/IP of the Matrix. Needs to be specified together with -p/--port " +
				"By manually Specifying the Matrix Host/Port, Auto-Discovery will be disabled and only " +
				"this Host/Port combination will be tried"));

		options.addOption(new Option("p", "port", true,
			"Port of the Matrix. Needs to be specified together with -h/--host"));

		options.addOption(new Option("i", "client-id", true,
			"Client-Id of this Client. If not specified, a random Client-Id will be generated on first Start " +
				"and stored on-disk. The Client-Id is used to identify the Client and map a Panel-Config to it."));

		options.addOption(new Option("b", "buttons", true,
			"List of Button-Names, separated by Commas. Used to Test different Button-Configurations"));

		CommandLineParser parser = new DefaultParser();

		try {
			log.info("Parsing Comand-Line Arguments");
			commandLine = parser.parse(options, arguments.getSourceArgs());

			validate();
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar debugclient.jar", options);

			System.exit(1);
			throw e;
		}
	}

	private void validate() throws ParseException {
		boolean hasMatrixHost = getMatrixHost().isPresent();
		boolean hasMatrixPort = getMatrixPort().isPresent();
		if (hasMatrixHost != hasMatrixPort) {
			throw new ParseException("--host must be used together with --port and vice versa.");
		}
	}

	public Optional<InetAddress> getMatrixHost() {
		return Optional.ofNullable(commandLine.getOptionValue("host"))
			.flatMap(this::tryParseInetAddress);
	}

	public Optional<Integer> getMatrixPort() {
		return Optional.ofNullable(commandLine.getOptionValue("port"))
			.map(Integer::new);
	}

	private Optional<InetAddress> tryParseInetAddress(String hostnameOrIp) {
		try {
			return Optional.of(InetAddress.getByName(hostnameOrIp));
		} catch (UnknownHostException e) {
			return Optional.empty();
		}
	}

	public boolean hasManualMatrixAddress() {
		return getMatrixHost().isPresent() && getMatrixPort().isPresent();
	}

	public Optional<String> getClientId() {
		return Optional.ofNullable(commandLine.getOptionValue("client-id"));
	}

	public Optional<List<String>> getButtons() {
		return Optional.ofNullable(commandLine.getOptionValue("buttons"))
			.map(this::splitButtonsString);
	}

	private List<String> splitButtonsString(String buttons) {
		return Splitter.on(',')
			.omitEmptyStrings()
			.trimResults()
			.splitToList(buttons);
	}
}
