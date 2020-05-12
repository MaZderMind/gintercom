package de.mazdermind.gintercom.matrix.restapi.statistics.history;

import java.util.List;

import de.mazdermind.gintercom.matrix.restapi.statistics.StatisticsDto;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class HistoryDto {
	private List<StatisticsDto> minutely;
	private List<StatisticsDto> quarterHourly;
	private List<StatisticsDto> hourly;
}
