package de.mazdermind.gintercom.matrix.configuration.model;

import static de.mazdermind.gintercom.matchers.ValidatesMatcher.validates;
import static de.mazdermind.gintercom.utils.JsonMapUtils.convertJsonTo;
import static de.mazdermind.gintercom.utils.JsonMapUtils.getJsonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.mazdermind.gintercom.utils.JsonMapUtils;

public class ButtonSetConfigTest {

	private Map<String, Object> testJson;
	private Map<String, Object> buttonJson;

	@Before
	public void prepare() {
		testJson = JsonMapUtils.readTomlToMap("config/model/buttonset.toml");

		Map<String, Object> buttonsJson = getJsonMap(testJson, "buttons");
		buttonJson = getJsonMap(buttonsJson, "1");
	}

	@Test
	public void deserializesCorrectly() {
		ButtonSetConfig buttonSetConfig = convertJsonTo(ButtonSetConfig.class, testJson);
		assertThat(buttonSetConfig.getButtons(), notNullValue());
		assertThat(buttonSetConfig.getButtons().size(), is(2));

		ButtonConfig buttonConfig1 = buttonSetConfig.getButtons().get("1");
		assertThat(buttonConfig1.getDisplay(), is("A/V Tech Broadcast"));
		assertThat(buttonConfig1.getAction(), is(ButtonAction.PTT));
		assertThat(buttonConfig1.getTargetType(), is(ButtonTargetType.GROUP));
		assertThat(buttonConfig1.getTarget(), is("av-tech"));

		ButtonConfig buttonConfig2 = buttonSetConfig.getButtons().get("2");
		assertThat(buttonConfig2.getDisplay(), is("Room A Broadcast"));
		assertThat(buttonConfig2.getAction(), is(ButtonAction.PTT));
		assertThat(buttonConfig2.getTargetType(), is(ButtonTargetType.GROUP));
		assertThat(buttonConfig2.getTarget(), is("room-a"));
	}

	@Test
	public void validationSucceeds() {
		ButtonSetConfig buttonSetConfig = convertJsonTo(ButtonSetConfig.class, testJson);
		assertThat(buttonSetConfig, validates());
	}

	@Test
	public void validationFailsWithoutButtonDisplay() {
		buttonJson.remove("display");

		PanelConfig panelConfig = convertJsonTo(PanelConfig.class, testJson);
		assertThat(panelConfig, not(validates()));
	}

	@Test
	public void validationFailsWithoutButtonAction() {
		buttonJson.remove("action");

		PanelConfig panelConfig = convertJsonTo(PanelConfig.class, testJson);
		assertThat(panelConfig, not(validates()));
	}

	@Test
	public void validationFailsWithoutButtonTargetType() {
		buttonJson.remove("targetType");

		PanelConfig panelConfig = convertJsonTo(PanelConfig.class, testJson);
		assertThat(panelConfig, not(validates()));
	}

	@Test
	public void validationFailsWithoutButtonTarget() {
		buttonJson.remove("target");

		PanelConfig panelConfig = convertJsonTo(PanelConfig.class, testJson);
		assertThat(panelConfig, not(validates()));
	}
}
