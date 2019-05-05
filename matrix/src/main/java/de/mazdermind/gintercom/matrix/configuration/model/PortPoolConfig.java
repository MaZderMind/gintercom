package de.mazdermind.gintercom.matrix.configuration.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Objects;

public class PortPoolConfig {
	@Valid
	@NotNull
	private Integer start;

	@Valid
	@NotNull
	private Integer limit;

	public Integer getStart() {
		return start;
	}

	public Integer getLimit() {
		return limit;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(start, limit);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PortPoolConfig that = (PortPoolConfig) o;
		return Objects.equal(start, that.start) &&
			Objects.equal(limit, that.limit);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("start", start)
			.append("limit", limit)
			.toString();
	}
}
