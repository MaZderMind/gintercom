package de.mazdermind.gintercom.matrix.portpool;

public class PortSetPoolFactory {
	private static PortSetPool instance;

	private PortSetPoolFactory() {
	}

	public static PortSetPool getInstance() {
		if (instance == null) {
			instance = new PortSetPool(
					new PortPoolConfig().setStart(40000).setLimit(1000),
					new PortPoolConfig().setStart(50000).setLimit(1000)
			);
		}

		return instance;
	}
}
