package de.mazdermind.gintercom.debugclient.gui;

import static de.mazdermind.gintercom.debugclient.gui.GroupButtonGridManager.calcularNumberOfRows;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class GroupButtonGridManagerTest {
	@Test
	public void calculateRows() {
		assertThat(calcularNumberOfRows(0, 2), is(0));

		assertThat(calcularNumberOfRows(1, 2), is(1));
		assertThat(calcularNumberOfRows(2, 2), is(1));
		assertThat(calcularNumberOfRows(3, 2), is(2));
		assertThat(calcularNumberOfRows(4, 2), is(2));
		assertThat(calcularNumberOfRows(5, 2), is(3));
		assertThat(calcularNumberOfRows(6, 2), is(3));

		assertThat(calcularNumberOfRows(12, 2), is(6));
	}
}
