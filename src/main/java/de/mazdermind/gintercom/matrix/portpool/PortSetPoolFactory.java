package de.mazdermind.gintercom.matrix.portpool;

public class PortSetPoolFactory {
	private static PortSetPool instance;

	private PortSetPoolFactory() {
	}

	public static PortSetPool getInstance() {
		if (instance == null) {
			instance = new PortSetPool(
					new PortPoolConfig().setStart(10000).setLimit(9999),
					new PortPoolConfig().setStart(20000).setLimit(9999)
			);
		}

		return instance;
	}
}
