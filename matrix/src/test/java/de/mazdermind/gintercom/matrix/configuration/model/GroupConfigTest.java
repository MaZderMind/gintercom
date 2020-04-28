package de.mazdermind.gintercom.matrix.configuration.model;

import static de.mazdermind.gintercom.testutils.JsonMapUtils.convertJsonTo;
import static de.mazdermind.gintercom.testutils.assertations.IsValidCondition.VALID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.mazdermind.gintercom.testutils.JsonMapUtils;

public class GroupConfigTest {

	private Map<String, Object> testJson;

	@Before
	public void prepare() {
		testJson = JsonMapUtils.readTomlToMap("config/model/group.toml");
	}

	@Test
	public void deserializesCorrectly() {
		GroupConfig groupConfig = convertJsonTo(GroupConfig.class, testJson);
		assertThat(groupConfig.getDisplay()).isEqualTo("A/V Tech");
	}

	@Test
	public void validationSucceedsWithMinimalValidConfig() {
		GroupConfig groupConfig = convertJsonTo(GroupConfig.class, testJson);
		assertThat(groupConfig).is(VALID);
	}

	@Test
	public void validationFailsWithoutDisplay() {
		testJson.remove("display");
		GroupConfig groupConfig = convertJsonTo(GroupConfig.class, testJson);
		assertThat(groupConfig).isNot(VALID);
	}

}
