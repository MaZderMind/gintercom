package de.mazdermind.gintercom.mixingcore.portpool;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortPool {
	private static final Logger log = LoggerFactory.getLogger(PortPool.class);
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
