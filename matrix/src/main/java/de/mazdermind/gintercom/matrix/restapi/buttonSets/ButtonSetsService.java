package de.mazdermind.gintercom.matrix.restapi.buttonSets;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import de.mazdermind.gintercom.matrix.configuration.model.Config;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ButtonSetsService {
	private final Config config;

	public Stream<ButtonSetDto> getConfiguredButtonSets() {
		return config.getButtonSets().entrySet().stream()
			.sorted(Map.Entry.comparingByKey())
			.map(entry -> new ButtonSetDto(entry.getKey())
				.setButtons(entry.getValue().getButtons().entrySet().stream()
					.sorted(Map.Entry.comparingByKey())
					.map(buttonEntry -> new ButtonDto(buttonEntry.getKey(), buttonEntry.getValue()))
					.collect(Collectors.toList())));
	}
}
