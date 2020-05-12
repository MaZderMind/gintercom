package de.mazdermind.gintercom.matrix.restapi.statistics;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rest/statistics")
@RequiredArgsConstructor
public class StatisticsWebservice {
	private final StatisticsService statisticsService;

	@GetMapping
	public StatisticsDto getStatistics() {
		return statisticsService.collectStatistics();
	}
}
