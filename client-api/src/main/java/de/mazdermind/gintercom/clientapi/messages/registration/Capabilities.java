package de.mazdermind.gintercom.clientapi.messages.registration;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Capabilities {
	@NotEmpty
	private List<String> buttons = new ArrayList<>();
}
