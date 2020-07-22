package de.mazdermind.gintercom.debugclient.gui;

import static de.mazdermind.gintercom.debugclient.gui.ActionButtonGridManager.calculateNumberOfRows;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ActionButtonGridManagerTest {
	@Test
	public void calculateRows() {
		assertThat(calculateNumberOfRows(0, 2)).isEqualTo(0);

		assertThat(calculateNumberOfRows(1, 2)).isEqualTo(1);
		assertThat(calculateNumberOfRows(2, 2)).isEqualTo(1);
		assertThat(calculateNumberOfRows(3, 2)).isEqualTo(2);
		assertThat(calculateNumberOfRows(4, 2)).isEqualTo(2);
		assertThat(calculateNumberOfRows(5, 2)).isEqualTo(3);
		assertThat(calculateNumberOfRows(6, 2)).isEqualTo(3);

		assertThat(calculateNumberOfRows(12, 2)).isEqualTo(6);
	}
}
