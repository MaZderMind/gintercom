package de.mazdermind.gintercom.matrix.restapi.statistics;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class StatisticsDto {
	private List<String> groupsConfigured;

	private List<String> panelsConfigured;
	private List<String> panelsAssigned;
	private List<String> panelsOnline;

	private List<String> devicesOnline;
	private List<String> devicesProvisioned;
}
