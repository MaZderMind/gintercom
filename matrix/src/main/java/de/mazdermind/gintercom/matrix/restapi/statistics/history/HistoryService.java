package de.mazdermind.gintercom.matrix.restapi.statistics.history;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import de.mazdermind.gintercom.matrix.restapi.statistics.StatisticsDto;
import de.mazdermind.gintercom.matrix.restapi.statistics.StatisticsService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryService {
	public static final int MAX_MINUTELY_VALUES = 90;
	public static final int MAX_QUARTER_HOURLY_VALUES = 96;
	public static final int MAX_HOURLY_VALUES = 96;

	private final CircularFifoQueue<StatisticsDto> minutelyRecords = new CircularFifoQueue<>(MAX_MINUTELY_VALUES);
	private final CircularFifoQueue<StatisticsDto> quarterHourlyRecords = new CircularFifoQueue<>(MAX_QUARTER_HOURLY_VALUES);
	private final CircularFifoQueue<StatisticsDto> hourlyRecords = new CircularFifoQueue<>(MAX_HOURLY_VALUES);

	private final StatisticsService statisticsService;

	/**
	 * Record a Value every Minute for the last 90 Minutes (90 Records)
	 */
	@Scheduled(fixedRateString = "PT1M")
	public void recordEveryMinute() {
		StatisticsDto statistics = statisticsService.collectStatistics();
		minutelyRecords.add(statistics);
	}

	/**
	 * Record a Value every 15 Minutes for the last 24 Hours (96 Records)
	 */
	@Scheduled(fixedRateString = "PT20M")
	public void recordEveryQuarterHour() {
		StatisticsDto statistics = statisticsService.collectStatistics();
		quarterHourlyRecords.add(statistics);
	}

	/**
	 * Record a Value every Hour for the last 4 Days (96 Records)
	 */
	@Scheduled(fixedRateString = "PT1H")
	public void recordEveryHour() {
		StatisticsDto statistics = statisticsService.collectStatistics();
		hourlyRecords.add(statistics);
	}

	public List<StatisticsDto> getMinutelyRecords() {
		return new ArrayList<>(minutelyRecords);
	}

	public List<StatisticsDto> getQuarterHourlyRecords() {
		return new ArrayList<>(quarterHourlyRecords);
	}

	public List<StatisticsDto> getHourlyRecords() {
		return new ArrayList<>(hourlyRecords);
	}
}
