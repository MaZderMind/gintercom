package de.mazdermind.gintercom.matrix.configuration.evaluation;

import static org.assertj.core.api.Assertions.assertThat;

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

public class UsageAnalyzerTest {
	private Config config;

	@Before
	public void before() {
		config = new Config()
			.setGroups(ImmutableMap.of(
				"g1", new GroupConfig(),
				"g2", new GroupConfig(),
				"g3", new GroupConfig(),
				"g4", new GroupConfig(),
				"g5", new GroupConfig()
			))
			.setPanels(ImmutableMap.of(
				"p1", new PanelConfig()
					.setClientId("TEST-0001")
					.setButtons(ImmutableMap.of(
						"bg1", new ButtonConfig()
							.setTarget("g1")
							.setTargetType(CommunicationTargetType.GROUP),
						"bp2", new ButtonConfig()
							.setTarget("p2")
							.setTargetType(CommunicationTargetType.PANEL)
					)),
				"p2", new PanelConfig()
					.setClientId("TEST-0002")
					.setRxGroups(ImmutableSet.of("g2"))
					.setTxGroups(ImmutableSet.of("g3"))
					.setButtonSets(ImmutableList.of("bs1")),
				"p3", new PanelConfig()
			))
			.setButtonSets(ImmutableMap.of(
				"bs1", new ButtonSetConfig()
					.setButtons(ImmutableMap.of(
						"bsg", new ButtonConfig()
							.setTargetType(CommunicationTargetType.GROUP)
							.setTarget("g4"),
						"bsp", new ButtonConfig()
							.setTargetType(CommunicationTargetType.PANEL)
							.setTarget("p3")
					)))
			);
	}

	@Test
	public void getGroupUsers() {
		assertThat(UsageAnalyzer.getGroupUsages(config, "g1"))
			.hasOnlyOneElementSatisfying(usage ->
				assertThat(usage)
					.returns(ConfigObjectType.PANEL, Usage::getUserType)
					.returns("p1", Usage::getUserId)
					.returns("Group g1 is used as target of Button bg1 of Panel p1", Usage::getUsageDescription));

		assertThat(UsageAnalyzer.getGroupUsages(config, "g2"))
			.hasOnlyOneElementSatisfying(usage ->
				assertThat(usage)
					.returns(ConfigObjectType.PANEL, Usage::getUserType)
					.returns("p2", Usage::getUserId)
					.returns("Group g2 is used as rxGroup of Panel p2", Usage::getUsageDescription));

		assertThat(UsageAnalyzer.getGroupUsages(config, "g3"))
			.hasOnlyOneElementSatisfying(usage ->
				assertThat(usage)
					.returns(ConfigObjectType.PANEL, Usage::getUserType)
					.returns("p2", Usage::getUserId)
					.returns("Group g3 is used as txGroup of Panel p2", Usage::getUsageDescription));

		assertThat(UsageAnalyzer.getGroupUsages(config, "g4"))
			.hasOnlyOneElementSatisfying(usage ->
				assertThat(usage)
					.returns(ConfigObjectType.BUTTON_SET, Usage::getUserType)
					.returns("bs1", Usage::getUserId)
					.returns("Group g4 is used as target of Button bsg of ButtonSet bs1", Usage::getUsageDescription));

		assertThat(UsageAnalyzer.getGroupUsages(config, "g5")).isEmpty();
	}
}
