package de.mazdermind.gintercom.debugclient.gui;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class AudioLevelDisplayManagerTest {
	@Test
	public void normalizeDb() {
		final double TOLERABLE_ERROR = 0.1;

		assertThat(AudioLevelDisplayManager.normalizeDb(-60.0), closeTo(0.0, TOLERABLE_ERROR));
		assertThat(AudioLevelDisplayManager.normalizeDb(-30.0), closeTo(0.25, TOLERABLE_ERROR));
		assertThat(AudioLevelDisplayManager.normalizeDb(-15.0), closeTo(0.5, TOLERABLE_ERROR));
		assertThat(AudioLevelDisplayManager.normalizeDb(-5.0), closeTo(0.75, TOLERABLE_ERROR));
		assertThat(AudioLevelDisplayManager.normalizeDb(-0.0), closeTo(1.00, TOLERABLE_ERROR));
	}

	@Test
	public void clamp() {
		final double TOLERABLE_ERROR = 0.00001;
		assertThat(AudioLevelDisplayManager.clamp(-999.0), closeTo(0, TOLERABLE_ERROR));
		assertThat(AudioLevelDisplayManager.clamp(-1.0), closeTo(0, TOLERABLE_ERROR));
		assertThat(AudioLevelDisplayManager.clamp(-0.01), closeTo(0, TOLERABLE_ERROR));
		assertThat(AudioLevelDisplayManager.clamp(0.0), closeTo(0, TOLERABLE_ERROR));
		assertThat(AudioLevelDisplayManager.clamp(0.001), closeTo(0.001, TOLERABLE_ERROR));
		assertThat(AudioLevelDisplayManager.clamp(0.25), closeTo(0.25, TOLERABLE_ERROR));
		assertThat(AudioLevelDisplayManager.clamp(0.999), closeTo(0.999, TOLERABLE_ERROR));
		assertThat(AudioLevelDisplayManager.clamp(1.0), closeTo(1.0, TOLERABLE_ERROR));
		assertThat(AudioLevelDisplayManager.clamp(1.1), closeTo(1.0, TOLERABLE_ERROR));
		assertThat(AudioLevelDisplayManager.clamp(999.0), closeTo(1.0, TOLERABLE_ERROR));
	}

	@Test
	public void scaleDecibelLevelToHeightToleratesZeroHeight() {
		assertThat(AudioLevelDisplayManager.scaleDecibelLevelToHeight(0, singletonList(-42.0)), contains(0));
	}

	@Test
	public void scaleDecibelLevelToHeight() {
		final int HEIGHT = 100;
		List<Double> decibels = ImmutableList.of(-60.0, -15.0);

		assertThat(AudioLevelDisplayManager.scaleDecibelLevelToHeight(HEIGHT, decibels), contains(0, 49));
	}

	@Test
	public void calculateChannelWidthToleratesZeroWidth() {
		assertThat(AudioLevelDisplayManager.calculateChannelWidth(0, 2), is(0));
	}

	@Test
	public void calculateChannelWidth() {
		// naturally round results
		assertThat(AudioLevelDisplayManager.calculateChannelWidth(40, 1), is(40));
		assertThat(AudioLevelDisplayManager.calculateChannelWidth(41, 2), is(20));
		assertThat(AudioLevelDisplayManager.calculateChannelWidth(43, 4), is(10));

		// odd, rounded results
		assertThat(AudioLevelDisplayManager.calculateChannelWidth(40, 2), is(20 /* 19.5 */));
		assertThat(AudioLevelDisplayManager.calculateChannelWidth(40, 4), is(9 /* 9.25 */));
	}

	@Test
	public void calculateChannelHorizontalOffset() {
		/// naturally round results
		// channel #1/1 from 0-40 px
		assertThat(AudioLevelDisplayManager.calculateChannelHorizontalOffset(40, 1, 0), is(0));

		// channel #1/2 from 0-20 px
		assertThat(AudioLevelDisplayManager.calculateChannelHorizontalOffset(41, 2, 0), is(0));
		// channel #2/2 from 21-41 px
		assertThat(AudioLevelDisplayManager.calculateChannelHorizontalOffset(41, 2, 1), is(21));

		// channel #1/4 from 0-10 px
		assertThat(AudioLevelDisplayManager.calculateChannelHorizontalOffset(43, 4, 0), is(0));
		// channel #2/4 from 11-21 px
		assertThat(AudioLevelDisplayManager.calculateChannelHorizontalOffset(43, 4, 1), is(11));
		// channel #3/4 from 22-32 px
		assertThat(AudioLevelDisplayManager.calculateChannelHorizontalOffset(43, 4, 2), is(22));
		// channel #4/4 from 33-43 px
		assertThat(AudioLevelDisplayManager.calculateChannelHorizontalOffset(43, 4, 3), is(33));


		/// odd, rounded results
		// channel #1/2 from 0-19.5 px
		assertThat(AudioLevelDisplayManager.calculateChannelHorizontalOffset(40, 2, 0), is(0));
		// channel #2/2 from 20.5-40 px
		assertThat(AudioLevelDisplayManager.calculateChannelHorizontalOffset(40, 2, 1), is(21));

		// channel #1/4 from 0-9.25 px
		assertThat(AudioLevelDisplayManager.calculateChannelHorizontalOffset(40, 4, 0), is(0));
		// channel #2/4 from 10.25-19.5 px
		assertThat(AudioLevelDisplayManager.calculateChannelHorizontalOffset(40, 4, 1), is(10));
		// channel #3/4 from 20.5-29.75 px
		assertThat(AudioLevelDisplayManager.calculateChannelHorizontalOffset(40, 4, 2), is(21));
		// channel #4/4 from 30.75-40 px
		assertThat(AudioLevelDisplayManager.calculateChannelHorizontalOffset(40, 4, 3), is(31));
	}
}
