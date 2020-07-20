package de.mazdermind.gintercom.matrix.configuration.model;

import static de.mazdermind.gintercom.testutils.JsonMapUtils.convertJsonTo;
import static de.mazdermind.gintercom.testutils.assertations.IsValidCondition.VALID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.mazdermind.gintercom.clientapi.configuration.ButtonAction;
import de.mazdermind.gintercom.clientapi.configuration.ButtonConfig;
import de.mazdermind.gintercom.clientapi.configuration.ButtonTargetType;
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
		assertThat(panelConfig.getDisplay()).isEqualTo("A/V Tech Romm A");
		assertThat(panelConfig.getClientId()).isEqualTo("DEAD-BEEF");
		assertThat(panelConfig.getRxGroups()).containsOnly("room-a", "av-tech");
		assertThat(panelConfig.getTxGroups()).containsOnly("av-tech");
		assertThat(panelConfig.getButtonsets()).containsOnly("av-tech");

		assertThat(panelConfig.getButtons()).containsKey("6");
		ButtonConfig buttonConfig = panelConfig.getButtons().get("6");
		assertThat(buttonConfig.getDisplay()).isEqualTo("Room A Broadcast");
		assertThat(buttonConfig.getAction()).isEqualTo(ButtonAction.PTT);
		assertThat(buttonConfig.getTargetType()).isEqualTo(ButtonTargetType.GROUP);
		assertThat(buttonConfig.getTarget()).isEqualTo("room-a");
	}

	@Test
	public void deserializesMinimalConfigCorrectly() {
		PanelConfig panelConfig = convertJsonTo(PanelConfig.class, testJsonMinimal);
		assertThat(panelConfig.getDisplay()).isEqualTo("A/V Tech Romm A");
		assertThat(panelConfig.getClientId()).isNull();
		assertThat(panelConfig.getRxGroups()).isEmpty();
		assertThat(panelConfig.getTxGroups()).isEmpty();
		assertThat(panelConfig.getButtonsets()).isEmpty();

		assertThat(panelConfig.getButtons()).isNotNull();
		assertThat(panelConfig.getButtons().size()).isEqualTo(0);
	}

	@Test
	public void validationSucceeds() {
		PanelConfig panelConfig = convertJsonTo(PanelConfig.class, testJsonFull);
		assertThat(panelConfig).is(VALID);
	}

	@Test
	public void validationSucceedsWithMinimalValidConfig() {
		PanelConfig panelConfig = convertJsonTo(PanelConfig.class, testJsonMinimal);
		assertThat(panelConfig).is(VALID);
	}

	@Test
	public void validationFailsWithoutDisplay() {
		testJsonFull.remove("display");
		PanelConfig panelConfig = convertJsonTo(PanelConfig.class, testJsonFull);
		assertThat(panelConfig).isNot(VALID);
	}

	@Test
	public void validationFailsWithoutButtonDisplay() {
		buttonJson.remove("display");

		PanelConfig panelConfig = convertJsonTo(PanelConfig.class, testJsonFull);
		assertThat(panelConfig).isNot(VALID);
	}

	@Test
	public void validationFailsWithoutButtonAction() {
		buttonJson.remove("action");

		PanelConfig panelConfig = convertJsonTo(PanelConfig.class, testJsonFull);
		assertThat(panelConfig).isNot(VALID);
	}

	@Test
	public void validationFailsWithoutButtonTargetType() {
		buttonJson.remove("targetType");

		PanelConfig panelConfig = convertJsonTo(PanelConfig.class, testJsonFull);
		assertThat(panelConfig).isNot(VALID);
	}

	@Test
	public void validationFailsWithoutButtonTarget() {
		buttonJson.remove("target");

		PanelConfig panelConfig = convertJsonTo(PanelConfig.class, testJsonFull);
		assertThat(panelConfig).isNot(VALID);
	}
}
