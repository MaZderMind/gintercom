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
		assertThat(buttonSetConfig.getButtons()).isNotNull();
		assertThat(buttonSetConfig.getButtons()).hasSize(2);

		ButtonConfig buttonConfig1 = buttonSetConfig.getButtons().get("1");
		assertThat(buttonConfig1.getDisplay()).isEqualTo("A/V Tech Broadcast");
		assertThat(buttonConfig1.getAction()).isEqualTo(ButtonAction.PUSH);
		assertThat(buttonConfig1.getTargetType()).isEqualTo(ButtonTargetType.GROUP);
		assertThat(buttonConfig1.getTarget()).isEqualTo("av-tech");

		ButtonConfig buttonConfig2 = buttonSetConfig.getButtons().get("2");
		assertThat(buttonConfig2.getDisplay()).isEqualTo("Room A Broadcast");
		assertThat(buttonConfig2.getAction()).isEqualTo(ButtonAction.PUSH);
		assertThat(buttonConfig2.getTargetType()).isEqualTo(ButtonTargetType.GROUP);
		assertThat(buttonConfig2.getTarget()).isEqualTo("room-a");
	}

	@Test
	public void validationSucceeds() {
		ButtonSetConfig buttonSetConfig = convertJsonTo(ButtonSetConfig.class, testJson);
		assertThat(buttonSetConfig).is(VALID);
	}

	@Test
	public void validationFailsWithoutButtons() {
		testJson.remove("buttons");

		ButtonSetConfig buttonSetConfig = convertJsonTo(ButtonSetConfig.class, testJson);
		assertThat(buttonSetConfig).isNot(VALID);
	}

	@Test
	public void validationFailsWithoutButtonDisplay() {
		buttonJson.remove("display");

		ButtonSetConfig buttonSetConfig = convertJsonTo(ButtonSetConfig.class, testJson);
		assertThat(buttonSetConfig).isNot(VALID);
	}

	@Test
	public void validationFailsWithoutButtonAction() {
		buttonJson.remove("action");

		ButtonSetConfig buttonSetConfig = convertJsonTo(ButtonSetConfig.class, testJson);
		assertThat(buttonSetConfig).isNot(VALID);
	}

	@Test
	public void validationFailsWithoutButtonTargetType() {
		buttonJson.remove("targetType");

		ButtonSetConfig buttonSetConfig = convertJsonTo(ButtonSetConfig.class, testJson);
		assertThat(buttonSetConfig).isNot(VALID);
	}

	@Test
	public void validationFailsWithoutButtonTarget() {
		buttonJson.remove("target");

		ButtonSetConfig buttonSetConfig = convertJsonTo(ButtonSetConfig.class, testJson);
		assertThat(buttonSetConfig).isNot(VALID);
	}
}
