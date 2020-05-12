package de.mazdermind.gintercom.matrix.restapi.statistics;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class StatisticsDto {
	private long groupsConfigured;

	private long panelsConfigured;
	private long panelsAssigned;
	private long panelsUnassigned;
	private long panelsOnline;
	private long panelsOffline;

	private long devicesOnline;
	private long devicesProvisioned;
	private long devicesUnprovisioned;
}
