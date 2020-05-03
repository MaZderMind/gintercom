package de.mazdermind.gintercom.clientapi.messages.registration;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Objects;

public class Capabilities {
	@NotEmpty
	private List<String> buttons = new ArrayList<>();

	public List<String> getButtons() {
		return unmodifiableList(buttons);
	}

	public Capabilities setButtons(List<String> buttons) {
		this.buttons.clear();
		this.buttons.addAll(buttons);
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(buttons);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Capabilities that = (Capabilities) o;
		return Objects.equal(buttons, that.buttons);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("buttons", buttons)
			.toString();
	}
}
