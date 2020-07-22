package de.mazdermind.gintercom.matrix.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.mazdermind.gintercom.matrix.configuration.model.ButtonSetConfig;
import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.clientapi.configuration.ButtonConfig;

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
			.setButtonSets(ImmutableMap.of(
				"avCrews", avCrews,
				"orgaCrewa", orgaCrewa
			));
		buttonSetResolver = new ButtonSetResolver(config);
	}

	@Test
	public void handlesEmptyButtonList() {
		Map<String, ButtonConfig> buttons = buttonSetResolver.resolveButtons(new PanelConfig());
		assertThat(buttons).hasSize(0);
	}

	@Test
	public void panelOnlyButtonsAreApplied() {
		Map<String, ButtonConfig> buttons = buttonSetResolver.resolveButtons(new PanelConfig()
			.setButtons(ImmutableMap.of(
				"1", new ButtonConfig().setDisplay("Special Button"))
			));
		assertThat(buttons).hasSize(1);
		assertThat(buttons.get("1").getDisplay()).isEqualTo("Special Button");
	}

	@Test
	public void buttonSetOnlyButtonsAreApplied() {
		Map<String, ButtonConfig> buttons = buttonSetResolver.resolveButtons(new PanelConfig()
			.setButtonSets(ImmutableList.of("avCrews")));
		assertThat(buttons).hasSize(3);
		assertThat(buttons.get("1").getDisplay()).isEqualTo("A/V Broadcast");
		assertThat(buttons.get("2").getDisplay()).isEqualTo("Audio");
		assertThat(buttons.get("3").getDisplay()).isEqualTo("Video");
	}

	@Test
	public void panelButtonsOverrideButtonSetButtons() {
		Map<String, ButtonConfig> buttons = buttonSetResolver.resolveButtons(new PanelConfig()
			.setButtonSets(ImmutableList.of("avCrews"))
			.setButtons(ImmutableMap.of(
				"1", new ButtonConfig().setDisplay("Special Button"))
			));
		assertThat(buttons).hasSize(3);
		assertThat(buttons.get("1").getDisplay()).isEqualTo("Special Button");
		assertThat(buttons.get("2").getDisplay()).isEqualTo("Audio");
		assertThat(buttons.get("3").getDisplay()).isEqualTo("Video");
	}

	@Test
	public void laterButtonSetsOverrideEarlier() {
		Map<String, ButtonConfig> buttons = buttonSetResolver.resolveButtons(new PanelConfig()
			.setButtonSets(ImmutableList.of("avCrews", "orgaCrewa"))
			.setButtons(ImmutableMap.of(
				"1", new ButtonConfig().setDisplay("Special Button"))
			));
		assertThat(buttons).hasSize(4);
		assertThat(buttons.get("1").getDisplay()).isEqualTo("Special Button");
		assertThat(buttons.get("2").getDisplay()).isEqualTo("Audio");
		assertThat(buttons.get("3").getDisplay()).isEqualTo("Backstage");
		assertThat(buttons.get("4").getDisplay()).isEqualTo("Secu");
	}
}
