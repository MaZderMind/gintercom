package de.mazdermind.gintercom.matrix.configuration.model;

import static de.mazdermind.gintercom.testutils.JsonMapUtils.convertJsonTo;
import static de.mazdermind.gintercom.testutils.matchers.ValidatesMatcher.validates;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Before;
import org.junit.Test;

import de.mazdermind.gintercom.testutils.JsonMap;
import de.mazdermind.gintercom.testutils.JsonMapUtils;

public class ButtonSetConfigTest {

	private JsonMap testJson;
	private JsonMap buttonJson;

	@Before
	public void prepare() {
		testJson = JsonMapUtils.readTomlToMap("config/model/buttonset.toml");

		buttonJson = testJson.getObject("buttons").getObject("1");
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
