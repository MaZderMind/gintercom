package de.mazdermind.gintercom.matrix.restapi.buttonSets;

import java.util.stream.Stream;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rest/buttonSets")
@RequiredArgsConstructor
public class ButtonSetsWebservice {
	private final ButtonSetsService buttonSetsService;

	@GetMapping
	public Stream<ButtonSetDto> getConfiguredButtonSets() {
		return buttonSetsService.getConfiguredButtonSets();
	}
}
