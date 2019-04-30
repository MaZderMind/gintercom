package de.mazdermind.gintercom.matrix.configuration.model;

import static de.mazdermind.gintercom.matchers.ValidatesMatcher.validates;
import static de.mazdermind.gintercom.utils.JsonMapUtils.convertJsonTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.mazdermind.gintercom.utils.JsonMapUtils;

public class GroupConfigTest {

	private Map<String, Object> testJson;

	@Before
	public void prepare() {
		testJson = JsonMapUtils.readTomlToMap("config/model/group.toml");
	}

	@Test
	public void deserializesCorrectly() {
		GroupConfig groupConfig = convertJsonTo(GroupConfig.class, testJson);
		assertThat(groupConfig.getDisplay(), is("A/V Tech"));
	}

	@Test
	public void validationSucceedsWithMinimalValidConfig() {
		GroupConfig groupConfig = convertJsonTo(GroupConfig.class, testJson);
		assertThat(groupConfig, validates());
	}

	@Test
	public void validationFailsWithoutDisplay() {
		testJson.remove("display");
		GroupConfig groupConfig = convertJsonTo(GroupConfig.class, testJson);
		assertThat(groupConfig, not(validates()));
	}

}
