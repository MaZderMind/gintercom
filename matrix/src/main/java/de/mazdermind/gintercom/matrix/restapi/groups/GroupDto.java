package de.mazdermind.gintercom.matrix.restapi.groups;

import de.mazdermind.gintercom.matrix.configuration.model.GroupConfig;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GroupDto {
	private String id;
	private String display;

	public GroupDto(String id, GroupConfig config) {
		this.id = id;
		display = config.getDisplay();
	}
}
