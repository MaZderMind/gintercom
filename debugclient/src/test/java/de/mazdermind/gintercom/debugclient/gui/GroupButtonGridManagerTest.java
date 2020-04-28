package de.mazdermind.gintercom.debugclient.gui;

import static de.mazdermind.gintercom.debugclient.gui.GroupButtonGridManager.calcularNumberOfRows;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class GroupButtonGridManagerTest {
	@Test
	public void calculateRows() {
		assertThat(calcularNumberOfRows(0, 2)).isEqualTo(0);

		assertThat(calcularNumberOfRows(1, 2)).isEqualTo(1);
		assertThat(calcularNumberOfRows(2, 2)).isEqualTo(1);
		assertThat(calcularNumberOfRows(3, 2)).isEqualTo(2);
		assertThat(calcularNumberOfRows(4, 2)).isEqualTo(2);
		assertThat(calcularNumberOfRows(5, 2)).isEqualTo(3);
		assertThat(calcularNumberOfRows(6, 2)).isEqualTo(3);

		assertThat(calcularNumberOfRows(12, 2)).isEqualTo(6);
	}
}
