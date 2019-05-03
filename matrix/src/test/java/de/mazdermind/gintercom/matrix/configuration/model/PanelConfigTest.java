package de.mazdermind.gintercom.matrix.configuration.model;

import static de.mazdermind.gintercom.testutils.JsonMapUtils.convertJsonTo;
import static de.mazdermind.gintercom.testutils.matchers.ValidatesMatcher.validates;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Before;
import org.junit.Test;

import de.mazdermind.gintercom.testutils.JsonMap;
import de.mazdermind.gintercom.testutils.JsonMapUtils;

public class PanelConfigTest {

	private JsonMap testJsonFull;
	private JsonMap testJsonMinimal;

	private JsonMap buttonJson;

	@Before
	public void prepare() {
		testJsonFull = JsonMapUtils.readTomlToMap("config/model/panel-full.toml");
		testJsonMinimal = JsonMapUtils.readTomlToMap("config/model/panel-minimal.toml");

		buttonJson = testJsonFull.getObject("buttons").getObject("6");
	}


	@Test
	public void deserializesCorrectly() {
		PanelConfig panelConfig = convertJsonTo(PanelConfig.class, testJsonFull);
		assertThat(panelConfig.getDisplay(), is("A/V Tech Romm A"));
		assertThat(panelConfig.getRxGroups(), containsInAnyOrder("room-a", "av-tech"));
		assertThat(panelConfig.getTxGroups(), contains("av-tech"));
		assertThat(panelConfig.getButtonsets(), contains("av-tech"));

		assertThat(panelConfig.getButtons(), hasKey("6"));
		ButtonConfig buttonConfig = panelConfig.getButtons().get("6");
		assertThat(buttonConfig.getDisplay(), is("Room A Broadcast"));
		assertThat(buttonConfig.getAction(), is(ButtonAction.PTT));
		assertThat(buttonConfig.getTargetType(), is(ButtonTargetType.GROUP));
		assertThat(buttonConfig.getTarget(), is("room-a"));

		assertThat(panelConfig.getFixedIp().getIp().getHostAddress(), is("127.0.0.1"));
		assertThat(panelConfig.getFixedIp().getMatrixPort(), is(10004));
		assertThat(panelConfig.getFixedIp().getClientPort(), is(20004));
	}

	@Test
	public void deserializesMinimalConfigCorrectly() {
		PanelConfig panelConfig = convertJsonTo(PanelConfig.class, testJsonMinimal);
		assertThat(panelConfig.getDisplay(), is("A/V Tech Romm A"));
		assertThat(panelConfig.getRxGroups(), empty());
		assertThat(panelConfig.getTxGroups(), empty());
		assertThat(panelConfig.getButtonsets(), empty());

		assertThat(panelConfig.getButtons(), notNullValue());
		assertThat(panelConfig.getButtons().size(), is(0));

		assertThat(panelConfig.hasFixedIp(), is(false));
		assertThat(panelConfig.getFixedIp(), nullValue());
	}

	@Test
	public void configValidates() {
		PanelConfig panelConfig = convertJsonTo(PanelConfig.class, testJsonFull);
		assertThat(panelConfig, validates());
	}

	@Test
	public void minimalConfigValidates() {
		PanelConfig panelConfig = convertJsonTo(PanelConfig.class, testJsonMinimal);
		assertThat(panelConfig, validates());
	}

	@Test
	public void validationFailsWithoutDisplay() {
		testJsonFull.remove("display");
		PanelConfig panelConfig = convertJsonTo(PanelConfig.class, testJsonFull);
		assertThat(panelConfig, not(validates()));
	}

	@Test
	public void validationFailsWithoutButtonDisplay() {
		buttonJson.remove("display");

		PanelConfig panelConfig = convertJsonTo(PanelConfig.class, testJsonFull);
		assertThat(panelConfig, not(validates()));
	}

	@Test
	public void validationFailsWithoutButtonAction() {
		buttonJson.remove("action");

		PanelConfig panelConfig = convertJsonTo(PanelConfig.class, testJsonFull);
		assertThat(panelConfig, not(validates()));
	}

	@Test
	public void validationFailsWithoutButtonTargetType() {
		buttonJson.remove("targetType");

		PanelConfig panelConfig = convertJsonTo(PanelConfig.class, testJsonFull);
		assertThat(panelConfig, not(validates()));
	}

	@Test
	public void validationFailsWithoutButtonTarget() {
		buttonJson.remove("target");

		PanelConfig panelConfig = convertJsonTo(PanelConfig.class, testJsonFull);
		assertThat(panelConfig, not(validates()));
	}
}
