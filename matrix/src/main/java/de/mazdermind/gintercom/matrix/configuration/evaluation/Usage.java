package de.mazdermind.gintercom.matrix.configuration.evaluation;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Usage {
	private ConfigObjectType userType;
	private String userId;
	private String usageDescription;
}
