package de.mazdermind.gintercom.matrix.configuration.evaluation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.validation.ValidationException;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.mazdermind.gintercom.clientapi.configuration.ButtonConfig;
import de.mazdermind.gintercom.clientapi.configuration.CommunicationTargetType;
import de.mazdermind.gintercom.matrix.configuration.model.ButtonSetConfig;
import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.configuration.model.GroupConfig;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;

public class ReferenceValidatorTest {
	private Config config;

	@Before
	public void before() {
		config = new Config()
			.setGroups(new HashMap<>(ImmutableMap.of(
				"g1", new GroupConfig(),
				"g2", new GroupConfig(),
				"g3", new GroupConfig(),
				"g4", new GroupConfig()
			)))
			.setPanels(new HashMap<>(ImmutableMap.of(
				"p1", new PanelConfig()
					.setClientId("TEST-0001")
					.setButtons(new HashMap<>(ImmutableMap.of(
						"bg1", new ButtonConfig()
							.setTarget("g1")
							.setTargetType(CommunicationTargetType.GROUP),
						"bp2", new ButtonConfig()
							.setTarget("p2")
							.setTargetType(CommunicationTargetType.PANEL)
					))),
				"p2", new PanelConfig()
					.setClientId("TEST-0002")
					.setRxGroups(new HashSet<>(ImmutableSet.of("g2")))
					.setTxGroups(new HashSet<>(ImmutableSet.of("g3")))
					.setButtonSets(new ArrayList<>(ImmutableList.of("bs1"))),
				"p3", new PanelConfig()
			)))
			.setButtonSets(new HashMap<>(ImmutableMap.of(
				"bs1", new ButtonSetConfig()
					.setButtons(new HashMap<>(ImmutableMap.of(
						"bsg", new ButtonConfig()
							.setTargetType(CommunicationTargetType.GROUP)
							.setTarget("g4"),
						"bsp", new ButtonConfig()
							.setTargetType(CommunicationTargetType.PANEL)
							.setTarget("p3")
					)))
			)));
	}

	@Test
	public void correctConfigPassesValidation() {
		ReferenceValidator.validateReferences(config);
	}

	@Test
	public void configFailsValidationWhenPanelButtonReferencesMissingGroup() {
		config.getGroups().remove("g1");

		assertThatThrownBy(() -> ReferenceValidator.validateReferences(config))
			.isInstanceOf(ValidationException.class)
			.hasMessage("Group g1 referenced as target from Button bg1 of Panel p1 does not exist");
	}

	@Test
	public void configFailsValidationWhenPanelReferencesMissingRxGroup() {
		config.getGroups().remove("g2");

		assertThatThrownBy(() -> ReferenceValidator.validateReferences(config))
			.isInstanceOf(ValidationException.class)
			.hasMessage("Group g2 referenced as rxGroup from Panel p2 does not exist");
	}

	@Test
	public void configFailsValidationWhenPanelReferencesMissingTxGroup() {
		config.getGroups().remove("g3");

		assertThatThrownBy(() -> ReferenceValidator.validateReferences(config))
			.isInstanceOf(ValidationException.class)
			.hasMessage("Group g3 referenced as txGroup from Panel p2 does not exist");
	}

	@Test
	public void configFailsValidationWhenPanelReferencesMissingButtonSet() {
		config.getButtonSets().remove("bs1");

		assertThatThrownBy(() -> ReferenceValidator.validateReferences(config))
			.isInstanceOf(ValidationException.class)
			.hasMessage("ButtonSet bs1 referenced from Panel p2 does not exist");
	}

	@Test
	public void configFailsValidationWhenPanelButtonReferencesMissingPanel() {
		config.getPanels().remove("p2");

		assertThatThrownBy(() -> ReferenceValidator.validateReferences(config))
			.isInstanceOf(ValidationException.class)
			.hasMessage("Panel p2 referenced as target from Button bp2 of Panel p1 does not exist");
	}

	@Test
	public void configFailsValidationWhenButtonSetReferencesMissingGroup() {
		config.getGroups().remove("g4");

		assertThatThrownBy(() -> ReferenceValidator.validateReferences(config))
			.isInstanceOf(ValidationException.class)
			.hasMessage("Group g4 referenced as target from Button bsg of ButtonSet bs1 does not exist");
	}

	@Test
	public void configFailsValidationWhenButtonSetReferencesMissingPanel() {
		config.getPanels().remove("p3");

		assertThatThrownBy(() -> ReferenceValidator.validateReferences(config))
			.isInstanceOf(ValidationException.class)
			.hasMessage("Panel p3 referenced as target from Button bsp of ButtonSet bs1 does not exist");
	}

	@Test
	public void configFailsValidationWhenClientIdIsUsedMultipleTimes() {
		config.getPanels().get("p1").setClientId("TEST-0000");
		config.getPanels().get("p2").setClientId("TEST-0000");

		assertThatThrownBy(() -> ReferenceValidator.validateReferences(config))
			.isInstanceOf(ValidationException.class)
			.hasMessage("Client-ID TEST-0000 referenced from Panel p2 is also referenced from Panel p1");
	}
}
