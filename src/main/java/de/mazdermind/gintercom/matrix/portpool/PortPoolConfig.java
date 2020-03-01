package de.mazdermind.gintercom.matrix.portpool;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Objects;

public class PortPoolConfig {
	private Integer start;

	private Integer limit;

	public Integer getStart() {
		return start;
	}

	public PortPoolConfig setStart(Integer start) {
		this.start = start;
		return this;
	}

	public Integer getLimit() {
		return limit;
	}

	public PortPoolConfig setLimit(Integer limit) {
		this.limit = limit;
		return this;
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
