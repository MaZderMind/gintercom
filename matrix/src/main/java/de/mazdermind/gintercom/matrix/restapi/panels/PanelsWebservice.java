package de.mazdermind.gintercom.matrix.restapi.panels;

import java.util.stream.Stream;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rest/panels")
@RequiredArgsConstructor
public class PanelsWebservice {
	private final PanelsService panelsService;

	@GetMapping
	private Stream<PanelInfoDto> getConfiguredPanels() {
		return panelsService.getConfiguredPanels();
	}

	@GetMapping("/assigned")
	private Stream<PanelInfoDto> getAssignedPanels() {
		return panelsService.getAssignedPanels();
	}

	@GetMapping("/unassigned")
	private Stream<PanelInfoDto> getUnassignedPanels() {
		return panelsService.getUnassignedPanels();
	}

	@GetMapping("/online")
	private Stream<PanelInfoDto> getOnlinePanels() {
		return panelsService.getOnlinePanels();
	}

	@GetMapping("/offline")
	private Stream<PanelInfoDto> getOfflinePanels() {
		return panelsService.getOfflinePanels();
	}
}
