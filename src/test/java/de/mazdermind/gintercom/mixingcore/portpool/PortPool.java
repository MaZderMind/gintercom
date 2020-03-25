package de.mazdermind.gintercom.mixingcore.portpool;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortPool {
	private static final Logger log = LoggerFactory.getLogger(PortPool.class);
	private final AtomicInteger nextPort;
	private final Boolean resetting;
	private final int start;
	private final int limit;

	public PortPool(PortPoolConfig config) {
		this(config.getStart(), config.getLimit(), config.getResetting());
	}

	public PortPool(int start, int limit, Boolean resetting) {
		this.start = start;
		this.limit = limit;
		nextPort = new AtomicInteger(start);
		this.resetting = resetting;
	}

	public int getNextPort() {
		int nextPort = this.nextPort.getAndIncrement();
		if (nextPort >= start + limit) {
			if (resetting) {
				log.warn("resetting from {} back to {}", nextPort, start);
				this.nextPort.set(start);
				return start;
			} else {
				throw new PoolExhaustedException(start, limit);
			}
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
