package de.mazdermind.gintercom.matrix.portpool;

import java.util.concurrent.atomic.AtomicInteger;

import de.mazdermind.gintercom.matrix.configuration.model.PortPoolConfig;

public class PortPool {
	private final AtomicInteger nextPort;
	private final int start;
	private final int limit;

	public PortPool(PortPoolConfig config) {
		this(config.getStart(), config.getLimit());
	}

	public PortPool(int start, int limit) {
		this.start = start;
		this.limit = limit;
		nextPort = new AtomicInteger(start);
	}

	public int getNextPort() {
		int nextPort = this.nextPort.getAndIncrement();
		if (nextPort >= start + limit) {
			throw new PoolExhaustedException(start, limit);
		}

		return nextPort;
	}

	public static class PoolExhaustedException extends RuntimeException {
		public PoolExhaustedException(int start, int limit) {
			super(String.format(
				"The Port-Pool starting at Port %d and limited to %s Ports is Exhausted.",
				start, limit
			));
		}
	}
}
