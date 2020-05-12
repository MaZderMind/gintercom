package de.mazdermind.gintercom.matrix.restapi.statistics.history;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rest/statistics/history")
@RequiredArgsConstructor
public class HistoryController {
	private final HistoryService historyService;

	@GetMapping
	public HistoryDto getHistoricData() {
		return new HistoryDto()
			.setMinutely(historyService.getMinutelyRecords())
			.setQuarterHourly(historyService.getQuarterHourlyRecords())
			.setHourly(historyService.getHourlyRecords());
	}
}
