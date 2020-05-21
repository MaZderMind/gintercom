package de.mazdermind.gintercom.matrix.restapi.statistics.history;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;

import de.mazdermind.gintercom.matrix.restapi.statistics.StatisticsDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rest/statistics/history")
@RequiredArgsConstructor
public class HistoryWebservice {
	private final HistoryService historyService;

	@GetMapping
	public Map<String, List<StatisticsDto>> getCombinedData() {
		return ImmutableMap.of(
			"minutely", getMinutelyStatistics(),
			"quarterHourly", getQuarterHourlyStatistics(),
			"hourly", getHourlyStatistics()
		);
	}

	@GetMapping("/minutely")
	public List<StatisticsDto> getMinutelyStatistics() {
		return historyService.getMinutelyRecords();
	}

	@GetMapping("/quarterHourly")
	public List<StatisticsDto> getQuarterHourlyStatistics() {
		return historyService.getQuarterHourlyRecords();
	}

	@GetMapping("/hourly")
	public List<StatisticsDto> getHourlyStatistics() {
		return historyService.getHourlyRecords();
	}
}
