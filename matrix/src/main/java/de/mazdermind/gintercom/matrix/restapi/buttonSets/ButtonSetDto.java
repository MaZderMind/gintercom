package de.mazdermind.gintercom.matrix.restapi.buttonSets;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ButtonSetDto {
	private String id;
	private List<ButtonDto> buttons;

	public ButtonSetDto(String id) {
		this.id = id;
	}
}
