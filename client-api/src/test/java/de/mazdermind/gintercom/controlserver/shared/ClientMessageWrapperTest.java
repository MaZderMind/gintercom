package de.mazdermind.gintercom.controlserver.shared;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.mazdermind.gintercom.clientapi.controlserver.messages.wrapper.WrappedClientMessage;
import de.mazdermind.gintercom.clientapi.controlserver.shared.ClientMessageWrapper;
import de.mazdermind.gintercom.clientapi.controlserver.shared.MalformedMessageException;
import lombok.Data;
import lombok.experimental.Accessors;

public class ClientMessageWrapperTest {
	private static final String HOST_ID = "0000-0000";

	private ClientMessageWrapper clientMessageWrapper;

	@Before
	public void setupWrapper() {
		clientMessageWrapper = new ClientMessageWrapper();
	}

	@Test
	public void canWrapClientMessages() {
		WrappedClientMessage<TestButtonPressedMessage> wrapped = clientMessageWrapper
			.wrap(new TestButtonPressedMessage().setButton("Q1"), HOST_ID);

		assertThat(wrapped).isNotNull();
		assertThat(wrapped).isInstanceOf(TestButtonPressedMessage.ClientMessage.class);
		assertThat(wrapped).isInstanceOf(WrappedClientMessage.class);
		assertThat(wrapped.getHostId()).isEqualTo(HOST_ID);

		assertThat(wrapped.getMessage()).isNotNull();
		assertThat(wrapped.getMessage()).isInstanceOf(TestButtonPressedMessage.class);
		assertThat(wrapped.getMessage().getButton()).isEqualTo("Q1");
	}

	@Test(expected = MalformedMessageException.class)
	public void rejectsMessagesWithoutInnerClientMessage() {
		clientMessageWrapper.wrap(new TestUnwrappedMessage(), HOST_ID);
	}

	@Data
	@Accessors(chain = true)
	public static class TestButtonPressedMessage {
		private String button;

		public static class ClientMessage extends WrappedClientMessage<TestButtonPressedMessage> {
		}
	}

	@Data
	@Accessors(chain = true)
	public static class TestUnwrappedMessage {
		private String foo;
	}
}
