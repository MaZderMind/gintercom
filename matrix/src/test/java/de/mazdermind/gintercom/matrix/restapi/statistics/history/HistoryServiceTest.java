package de.mazdermind.gintercom.matrix.restapi.statistics.history;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.mazdermind.gintercom.matrix.restapi.statistics.StatisticsDto;
import de.mazdermind.gintercom.matrix.restapi.statistics.StatisticsService;

@RunWith(MockitoJUnitRunner.class)
public class HistoryServiceTest {
	@Mock
	private StatisticsService statisticsService;

	@InjectMocks
	private HistoryService historyService;


	@Before
	public void before() {
		when(statisticsService.collectStatistics()).thenReturn(mock(StatisticsDto.class));
	}

	@Test
	public void collectsData() {
		for (int i = 0; i < 7; i++) {
			historyService.recordEveryMinute();
		}

		for (int i = 0; i < 5; i++) {
			historyService.recordEveryQuarterHour();
		}

		for (int i = 0; i < 3; i++) {
			historyService.recordEveryHour();
		}

		assertThat(historyService.getMinutelyRecords()).hasSize(7);
		assertThat(historyService.getQuarterHourlyRecords()).hasSize(5);
		assertThat(historyService.getHourlyRecords()).hasSize(3);
	}

	@Test
	public void limitsAmountOfData() {
		for (int i = 0; i < 250; i++) {
			historyService.recordEveryMinute();
			historyService.recordEveryQuarterHour();
			historyService.recordEveryHour();
		}

		assertThat(historyService.getMinutelyRecords()).hasSize(HistoryService.MAX_MINUTELY_VALUES);
		assertThat(historyService.getQuarterHourlyRecords()).hasSize(HistoryService.MAX_QUARTER_HOURLY_VALUES);
		assertThat(historyService.getHourlyRecords()).hasSize(HistoryService.MAX_HOURLY_VALUES);
	}
}
