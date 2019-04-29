package de.mazdermind.gintercom.debugclient.pipeline.audiolevel;

import org.freedesktop.gstreamer.Bus;
import org.freedesktop.gstreamer.message.Message;
import org.freedesktop.gstreamer.message.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AudioLevelMessageListener implements Bus.MESSAGE {
	private static final String ELEMENT_NAME = "audiolevel";
	private final AudioLevelEventEmitter eventEmitter;

	public AudioLevelMessageListener(
		@Autowired AudioLevelEventEmitter eventEmitter
	) {
		this.eventEmitter = eventEmitter;
	}

	@Override
	public void busMessage(Bus bus, Message message) {
		if (message.getType() == MessageType.ELEMENT && message.getSource().getName().equals(ELEMENT_NAME)) {
			publishAudioLevelEvent(message);
		}
	}

	private void publishAudioLevelEvent(Message message) {
		double[] peak = message.getStructure().getDoubles("peak");
		double[] decay = message.getStructure().getDoubles("decay");
		double[] rms = message.getStructure().getDoubles("rms");

		eventEmitter.emit(new AudioLevelEvent(peak, decay, rms));
	}
}
