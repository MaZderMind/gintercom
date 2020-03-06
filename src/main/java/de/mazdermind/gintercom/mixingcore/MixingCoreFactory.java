package de.mazdermind.gintercom.mixingcore;

public class MixingCoreFactory {
	private static MixingCore instance;

	private MixingCoreFactory() {
	}

	public static MixingCore getInstance() {
		if (instance == null) {
			instance = new MixingCore();
		}

		return instance;
	}
}
