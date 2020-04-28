package de.mazdermind.gintercom.debugclient.gui.components;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import java.util.List;

import org.assertj.core.data.Offset;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class AudioLevelDisplayTest {
	@Test
	public void normalizeDb() {
		final Offset<Double> TOLERABLE_ERROR = offset(0.1);

		assertThat(AudioLevelDisplay.normalizeDb(-60.0)).isCloseTo(0.0, TOLERABLE_ERROR);
		assertThat(AudioLevelDisplay.normalizeDb(-30.0)).isCloseTo(0.25, TOLERABLE_ERROR);
		assertThat(AudioLevelDisplay.normalizeDb(-15.0)).isCloseTo(0.5, TOLERABLE_ERROR);
		assertThat(AudioLevelDisplay.normalizeDb(-5.0)).isCloseTo(0.75, TOLERABLE_ERROR);
		assertThat(AudioLevelDisplay.normalizeDb(-0.0)).isCloseTo(1.00, TOLERABLE_ERROR);
	}

	@Test
	public void clamp() {
		final Offset<Double> TOLERABLE_ERROR = offset(0.00001);
		assertThat(AudioLevelDisplay.clamp(-999.0)).isCloseTo(0, TOLERABLE_ERROR);
		assertThat(AudioLevelDisplay.clamp(-1.0)).isCloseTo(0, TOLERABLE_ERROR);
		assertThat(AudioLevelDisplay.clamp(-0.01)).isCloseTo(0, TOLERABLE_ERROR);
		assertThat(AudioLevelDisplay.clamp(0.0)).isCloseTo(0, TOLERABLE_ERROR);
		assertThat(AudioLevelDisplay.clamp(0.001)).isCloseTo(0.001, TOLERABLE_ERROR);
		assertThat(AudioLevelDisplay.clamp(0.25)).isCloseTo(0.25, TOLERABLE_ERROR);
		assertThat(AudioLevelDisplay.clamp(0.999)).isCloseTo(0.999, TOLERABLE_ERROR);
		assertThat(AudioLevelDisplay.clamp(1.0)).isCloseTo(1.0, TOLERABLE_ERROR);
		assertThat(AudioLevelDisplay.clamp(1.1)).isCloseTo(1.0, TOLERABLE_ERROR);
		assertThat(AudioLevelDisplay.clamp(999.0)).isCloseTo(1.0, TOLERABLE_ERROR);
	}

	@Test
	public void scaleDecibelLevelToHeightToleratesZeroHeight() {
		assertThat(AudioLevelDisplay.scaleDecibelLevelToHeight(0, singletonList(-42.0))).containsExactly(0);
	}

	@Test
	public void scaleDecibelLevelToHeight() {
		final int HEIGHT = 100;
		List<Double> decibels = ImmutableList.of(-60.0, -15.0);

		assertThat(AudioLevelDisplay.scaleDecibelLevelToHeight(HEIGHT, decibels)).containsExactly(0, 49);
	}

	@Test
	public void calculateChannelWidthToleratesZeroWidth() {
		assertThat(AudioLevelDisplay.calculateChannelWidth(0, 2)).isEqualTo(0);
	}

	@Test
	public void calculateChannelWidth() {
		// naturally round results
		assertThat(AudioLevelDisplay.calculateChannelWidth(40, 1)).isEqualTo(40);
		assertThat(AudioLevelDisplay.calculateChannelWidth(41, 2)).isEqualTo(20);
		assertThat(AudioLevelDisplay.calculateChannelWidth(43, 4)).isEqualTo(10);

		// odd, rounded results
		assertThat(AudioLevelDisplay.calculateChannelWidth(40, 2)).isEqualTo(20 /* 19.5 */);
		assertThat(AudioLevelDisplay.calculateChannelWidth(40, 4)).isEqualTo(9 /* 9.25 */);
	}

	@Test
	public void calculateChannelHorizontalOffset() {
		/// naturally round results
		// channel #1/1 from 0-40 px
		assertThat(AudioLevelDisplay.calculateChannelHorizontalOffset(40, 1, 0)).isEqualTo(0);

		// channel #1/2 from 0-20 px
		assertThat(AudioLevelDisplay.calculateChannelHorizontalOffset(41, 2, 0)).isEqualTo(0);
		// channel #2/2 from 21-41 px
		assertThat(AudioLevelDisplay.calculateChannelHorizontalOffset(41, 2, 1)).isEqualTo(21);

		// channel #1/4 from 0-10 px
		assertThat(AudioLevelDisplay.calculateChannelHorizontalOffset(43, 4, 0)).isEqualTo(0);
		// channel #2/4 from 11-21 px
		assertThat(AudioLevelDisplay.calculateChannelHorizontalOffset(43, 4, 1)).isEqualTo(11);
		// channel #3/4 from 22-32 px
		assertThat(AudioLevelDisplay.calculateChannelHorizontalOffset(43, 4, 2)).isEqualTo(22);
		// channel #4/4 from 33-43 px
		assertThat(AudioLevelDisplay.calculateChannelHorizontalOffset(43, 4, 3)).isEqualTo(33);


		/// odd, rounded results
		// channel #1/2 from 0-19.5 px
		assertThat(AudioLevelDisplay.calculateChannelHorizontalOffset(40, 2, 0)).isEqualTo(0);
		// channel #2/2 from 20.5-40 px
		assertThat(AudioLevelDisplay.calculateChannelHorizontalOffset(40, 2, 1)).isEqualTo(21);

		// channel #1/4 from 0-9.25 px
		assertThat(AudioLevelDisplay.calculateChannelHorizontalOffset(40, 4, 0)).isEqualTo(0);
		// channel #2/4 from 10.25-19.5 px
		assertThat(AudioLevelDisplay.calculateChannelHorizontalOffset(40, 4, 1)).isEqualTo(10);
		// channel #3/4 from 20.5-29.75 px
		assertThat(AudioLevelDisplay.calculateChannelHorizontalOffset(40, 4, 2)).isEqualTo(21);
		// channel #4/4 from 30.75-40 px
		assertThat(AudioLevelDisplay.calculateChannelHorizontalOffset(40, 4, 3)).isEqualTo(31);
	}
}
