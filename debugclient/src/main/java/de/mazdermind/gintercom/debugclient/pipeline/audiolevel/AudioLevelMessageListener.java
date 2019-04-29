package de.mazdermind.gintercom.debugclient.pipeline.audiolevel;

import org.freedesktop.gstreamer.Bus;
import org.freedesktop.gstreamer.message.Message;
import org.freedesktop.gstreamer.message.MessageType;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.debugclient.util.EventEmitter;

@Component
public class AudioLevelMessageListener implements Bus.MESSAGE {
	private static final String ELEMENT_NAME = "audiolevel";

	private final EventEmitter<AudioLevelEvent> eventEmitter = new EventEmitter<>();

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

	public EventEmitter<AudioLevelEvent> getAudioLevelEventEmitter() {
		return eventEmitter;
	}
}
