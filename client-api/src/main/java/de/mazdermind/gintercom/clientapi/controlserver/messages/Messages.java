package de.mazdermind.gintercom.clientapi.controlserver.messages;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.AssociateMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.ClientHeartbeatMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.DeAssociateMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.ExampleMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.AssociatedMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.DeAssociatedMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.DeProvisionMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.ErrorMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.ExampleResponseMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.MatrixHeartbeatMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.ProvisionMessage;

/**
 * This Class lists allowed Messages for Matrix-to-Client and Client-to-Matrix communication.
 * <p>
 * Developer Comment: When it becomes necessary for Clients to add custom messages,
 * these Lists can be collected from Configuration-Beans, but for the Time being this is the most
 * straight forward way.
 */
public class Messages {
	public static final Map<String, Class<?>> MATRIX_TO_CLIENT = ImmutableList.of(
		AssociatedMessage.class,
		DeAssociatedMessage.class,
		ErrorMessage.class,
		ProvisionMessage.class,
		DeProvisionMessage.class,
		MatrixHeartbeatMessage.class,
		ExampleResponseMessage.class
	).stream().collect(Collectors.toMap(Class::getSimpleName, Function.identity()));

	public static final Map<String, Class<?>> CLIENT_TO_MATRIX = ImmutableList.of(
		AssociateMessage.class,
		DeAssociateMessage.class,
		ClientHeartbeatMessage.class,
		ExampleMessage.class
	).stream().collect(Collectors.toMap(Class::getSimpleName, Function.identity()));

	private Messages() {
	}
}
