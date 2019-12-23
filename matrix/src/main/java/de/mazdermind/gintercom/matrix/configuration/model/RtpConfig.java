package de.mazdermind.gintercom.matrix.configuration.model;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Objects;

public class RtpConfig {
	@NotNull
	private Long jitterbuffer;

	public Long getJitterbuffer() {
		return jitterbuffer;
	}

	public RtpConfig setJitterbuffer(Long jitterbuffer) {
		this.jitterbuffer = jitterbuffer;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(jitterbuffer);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RtpConfig rtpConfig = (RtpConfig) o;
		return Objects.equal(jitterbuffer, rtpConfig.jitterbuffer);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("jitterbuffer", jitterbuffer)
			.toString();
	}
}
