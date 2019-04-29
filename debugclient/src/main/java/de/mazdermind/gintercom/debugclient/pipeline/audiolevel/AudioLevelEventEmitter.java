package de.mazdermind.gintercom.debugclient.pipeline.audiolevel;

import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.debugclient.util.EventEmitter;

@Component
public class AudioLevelEventEmitter extends EventEmitter<AudioLevelEvent> {
}
