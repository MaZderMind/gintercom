package de.mazdermind.gintercom.matrix.configuration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.mazdermind.gintercom.matrix.configuration.model.ButtonSetConfig;
import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.shared.configuration.ButtonConfig;

public class ButtonSetResolverTest {
	private ButtonSetResolver buttonSetResolver;

	@Before
	public void prepare() {
		ButtonSetConfig avCrews = new ButtonSetConfig()
			.setButtons(ImmutableMap.of(
				"1", new ButtonConfig().setDisplay("A/V Broadcast"),
				"2", new ButtonConfig().setDisplay("Audio"),
				"3", new ButtonConfig().setDisplay("Video")
			));

		ButtonSetConfig orgaCrewa = new ButtonSetConfig()
			.setButtons(ImmutableMap.of(
				"3", new ButtonConfig().setDisplay("Backstage"),
				"4", new ButtonConfig().setDisplay("Secu")
			));

		Config config = new Config()
			.setButtonsets(ImmutableMap.of(
				"avCrews", avCrews,
				"orgaCrewa", orgaCrewa
			));
		buttonSetResolver = new ButtonSetResolver(config);
	}

	@Test
	public void handlesEmptyButtonList() {
		Map<String, ButtonConfig> buttons = buttonSetResolver.resolveButtons(new PanelConfig());
		assertThat(buttons.size(), is(0));
	}

	@Test
	public void panelOnlyButtonsAreApplied() {
		Map<String, ButtonConfig> buttons = buttonSetResolver.resolveButtons(new PanelConfig()
			.setButtons(ImmutableMap.of(
				"1", new ButtonConfig().setDisplay("Special Button"))
			));
		assertThat(buttons.size(), is(1));
		assertThat(buttons.get("1").getDisplay(), is("Special Button"));
	}

	@Test
	public void buttonsetOnlyButtonsAreApplied() {
		Map<String, ButtonConfig> buttons = buttonSetResolver.resolveButtons(new PanelConfig()
			.setButtonsets(ImmutableList.of("avCrews")));
		assertThat(buttons.size(), is(3));
		assertThat(buttons.get("1").getDisplay(), is("A/V Broadcast"));
		assertThat(buttons.get("2").getDisplay(), is("Audio"));
		assertThat(buttons.get("3").getDisplay(), is("Video"));
	}

	@Test
	public void panelButtonsOverrideButtonsetButtons() {
		Map<String, ButtonConfig> buttons = buttonSetResolver.resolveButtons(new PanelConfig()
			.setButtonsets(ImmutableList.of("avCrews"))
			.setButtons(ImmutableMap.of(
				"1", new ButtonConfig().setDisplay("Special Button"))
			));
		assertThat(buttons.size(), is(3));
		assertThat(buttons.get("1").getDisplay(), is("Special Button"));
		assertThat(buttons.get("2").getDisplay(), is("Audio"));
		assertThat(buttons.get("3").getDisplay(), is("Video"));
	}

	@Test
	public void laterButtonsetsOverrideEarlier() {
		Map<String, ButtonConfig> buttons = buttonSetResolver.resolveButtons(new PanelConfig()
			.setButtonsets(ImmutableList.of("avCrews", "orgaCrewa"))
			.setButtons(ImmutableMap.of(
				"1", new ButtonConfig().setDisplay("Special Button"))
			));
		assertThat(buttons.size(), is(4));
		assertThat(buttons.get("1").getDisplay(), is("Special Button"));
		assertThat(buttons.get("2").getDisplay(), is("Audio"));
		assertThat(buttons.get("3").getDisplay(), is("Backstage"));
		assertThat(buttons.get("4").getDisplay(), is("Secu"));
	}
}
