package de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Capabilities {
	@NotNull
	private List<String> buttons = new ArrayList<>();
}
