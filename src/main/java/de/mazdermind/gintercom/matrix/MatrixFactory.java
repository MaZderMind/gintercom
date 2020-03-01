package de.mazdermind.gintercom.matrix;

public class MatrixFactory {
	private static Matrix instance;

	private MatrixFactory() {
	}

	public static Matrix getInstance() {
		if (instance == null) {
			instance = new Matrix();
		}

		return instance;
	}
}
