package de.mazdermind.gintercom.mixingcore.portpool;

public class PortPoolConfig {
	private Integer start;
	private Integer limit;
	private Boolean resetting;

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

	public Boolean getResetting() {
		return resetting;
	}

	public PortPoolConfig setResetting(Boolean resetting) {
		this.resetting = resetting;
		return this;
	}
}
