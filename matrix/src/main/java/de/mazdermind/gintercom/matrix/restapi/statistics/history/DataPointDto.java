package de.mazdermind.gintercom.matrix.restapi.statistics.history;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DataPointDto {
	private long groupsConfigured;

	private long panelsConfigured;
	private long panelsAssigned;
	private long panelsOnline;

	private long devicesOnline;
	private long devicesProvisioned;
}
