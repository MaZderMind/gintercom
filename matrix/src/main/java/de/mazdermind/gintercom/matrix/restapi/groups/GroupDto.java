package de.mazdermind.gintercom.matrix.restapi.groups;

import de.mazdermind.gintercom.matrix.configuration.model.GroupConfig;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class GroupDto {
	private String id;
	private String display;

	public GroupDto(String id, GroupConfig config) {
		this.id = id;
		display = config.getDisplay();
	}
}
