package de.mazdermind.gintercom.matrix.mixingcore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import de.mazdermind.gintercom.mixingcore.Client;
import de.mazdermind.gintercom.mixingcore.Group;
import de.mazdermind.gintercom.mixingcore.MixingCore;

public class PanelGroupControllerTest {

	private PanelGroupController panelGroupController;
	private MixingCore mixingCore;

	@Before
	public void prepareMock() {
		mixingCore = mock(MixingCore.class);
		when(mixingCore.getGroupByName(anyString()))
			.thenAnswer(invocation -> mockGroup(invocation.getArgument(0)));

		panelGroupController = new PanelGroupController(mixingCore);
	}

	@Test
	public void calculateGroupsToAddWithEmptyActualSet() {
		Set<Group> groupsToAdd = panelGroupController.calculateGroupsToAdd(
			Collections.emptySet(),
			ImmutableSet.of("A", "B"));

		assertThat(groupsToAdd)
			.extracting(Group::getName)
			.containsOnly("A", "B");
	}

	@Test
	public void calculateGroupsToAdd() {
		Set<Group> groupsToAdd = panelGroupController.calculateGroupsToAdd(
			ImmutableSet.of(mockGroup("A"), mockGroup("C")),
			ImmutableSet.of("A", "B"));

		assertThat(groupsToAdd)
			.extracting(Group::getName)
			.containsOnly("B");
	}

	@Test
	public void calculateGroupsToAddWithEmptyDesiredSet() {
		Set<Group> groupsToAdd = panelGroupController.calculateGroupsToAdd(
			ImmutableSet.of(mockGroup("A"), mockGroup("C")),
			Collections.emptySet());

		assertThat(groupsToAdd).isEmpty();
	}

	@Test
	public void calculateGroupsToRemoveWithEmptyActualSet() {
		Set<Group> groupsToAdd = panelGroupController.calculateGroupsToRemove(
			Collections.emptySet(),
			ImmutableSet.of("A", "B"));

		assertThat(groupsToAdd).isEmpty();
	}

	@Test
	public void calculateGroupsToRemove() {
		Set<Group> groupsToAdd = panelGroupController.calculateGroupsToRemove(
			ImmutableSet.of(mockGroup("A"), mockGroup("C")),
			ImmutableSet.of("A", "B"));

		assertThat(groupsToAdd)
			.extracting(Group::getName)
			.containsOnly("C");
	}

	@Test
	public void calculateGroupsToRemoveWithEmptyDesiredSet() {
		Set<Group> groupsToAdd = panelGroupController.calculateGroupsToRemove(
			ImmutableSet.of(mockGroup("A"), mockGroup("C")),
			Collections.emptySet());

		assertThat(groupsToAdd)
			.extracting(Group::getName)
			.containsOnly("A", "C");
	}

	@Test
	public void reconcileGroups() {
		Client client = mock(Client.class);
		when(client.getName()).thenReturn("The Panel");

		Group groupA = mockGroup("A");
		Group groupB = mockGroup("B");
		Group groupC = mockGroup("C");
		Group groupX = mockGroup("X");
		Group groupY = mockGroup("Y");
		Group groupZ = mockGroup("Z");

		when(mixingCore.getGroupByName("C")).thenReturn(groupC);
		when(mixingCore.getGroupByName("X")).thenReturn(groupX);

		when(client.getRxGroups()).thenReturn(ImmutableSet.of(
			groupA,
			groupB
		));

		when(client.getTxGroups()).thenReturn(ImmutableSet.of(
			groupY,
			groupZ
		));

		panelGroupController.reconcileGroups(client,
			ImmutableSet.of("A", "C"),
			ImmutableSet.of("Z", "X"));

		verify(client).startReceivingFrom(groupC);
		verify(client).stopReceivingFrom(groupB);

		verify(client).startTransmittingTo(groupX);
		verify(client).stopTransmittingTo(groupY);
	}

	private Group mockGroup(String name) {
		Group group = mock(Group.class);
		when(group.getName()).thenReturn(name);
		return group;
	}
}
