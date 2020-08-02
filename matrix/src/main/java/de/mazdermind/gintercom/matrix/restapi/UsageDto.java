package de.mazdermind.gintercom.matrix.restapi;

import java.util.Set;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UsageDto {
	private Set<String> users;

	public boolean isUsed() {
		return !users.isEmpty();
	}
}
