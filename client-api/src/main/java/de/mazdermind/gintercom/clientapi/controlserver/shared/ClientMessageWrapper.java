package de.mazdermind.gintercom.clientapi.controlserver.shared;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.ExampleMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.wrapper.WrappedClientMessage;

/**
 * The ClientMessageWrapper takes any on of the Messages that a Client can send to the Matrix and wraps it ito a
 * {@link WrappedClientMessage} class.
 * <p>
 * When The Matrix receives a Message from a Client, for example an {@link ExampleMessage} it can't just distribute it in the
 * Matrix-Application using the Application-Events Mechanism because consumers of the Message would not know which Client the Message was
 * sent from. Therefore it needs to be wrapped into {@link ExampleMessage.ClientMessage}, which extends {@link WrappedClientMessage}.
 * <p>
 * Unfortunately because of Type-Erasure a Consumer cannot bind to
 * {@code @EventListener public void handleAssociationRequest(WrappedClientMessage<ExampleMessage> message)}
 * because such an Event-Handler would get <b>all</b> {@link WrappedClientMessage WrappedClientMessages}
 * <p>
 * Therefor all Client-to-Matrix Message-Classes are expected, to declare an inner class that extends {@link WrappedClientMessage}.
 * <p>
 * The {@link ClientMessageWrapper} finds this inner class for a Message-Class and creates an
 * instance of this class. Therefore Consumers can then bind to
 * {@code @EventListener public void handleAssociationRequest(ExampleMessage.ClientMessage message)} and filter this
 * way the specific type of Message desired.
 */
@Component
@SuppressWarnings({ "unchecked" })
public class ClientMessageWrapper {
	/**
	 * Wrap the Message {@code message} received from an associated Client with its inner {@code ClientMessage}-Class which is extending
	 * {@link WrappedClientMessage}.
	 *
	 * @param message The Message (received from an associated Client) to be wrapped
	 * @param hostId  The Host-ID this Message was received from
	 * @param <T>     The Type of the Message
	 * @return An Instance of the Messages' Client-Message Class wrapping {@code message}
	 */
	public <T> WrappedClientMessage<T> wrap(T message, String hostId) {
		return (WrappedClientMessage<T>) findWrappingClass(message.getClass(), WrappedClientMessage.class)
			.setMessage(message)
			.setHostId(hostId);
	}

	/**
	 * Looks up the correct Wrapping-Class from a Message-Class and creates an Instance of it
	 *
	 * @param messageClass      The Message-Class to look up the Wrapping-Class for
	 * @param wrappingClassType The desired Type of the Wrapping-Class
	 * @param <T>               The Type of the Message-Class
	 * @param <W>               The Type of the Wrapping-Class
	 * @return An instance of the Wrapping-Class
	 */
	private <W, T> W findWrappingClass(Class<T> messageClass, Class<W> wrappingClassType) {
		Class<?>[] innerClasses = messageClass.getDeclaredClasses();
		Optional<Class<W>> maybeWrappingClass = Arrays.stream(innerClasses)
			.filter(wrappingClassType::isAssignableFrom)
			.map(c -> (Class<W>) c)
			.findFirst();

		try {
			return maybeWrappingClass
				.orElseThrow(() -> new MalformedMessageException(
					String.format("The Message-Class %s does not correctly declare an Inner Class of Type %s",
						messageClass.getName(), wrappingClassType.getName())
				))
				.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new MalformedMessageException(
				String.format("Cannot create Inner Class of Type %s: %s",
					maybeWrappingClass.map(Class::getName).orElse(null), e.toString()
				));
		}
	}
}
