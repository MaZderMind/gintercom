package de.mazdermind.gintercom.clientsupport.pipeline;

import java.util.List;

import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientsupport.pipeline.audiosupport.AudioSystem;
import lombok.extern.slf4j.Slf4j;

/**
 * Default RTP/Audio-Pipeline for Intercom-Clients, auto-detects Audio-Subsystem of the OS and
 * chooses the default audio sink/source.
 * <p>
 * If your Client needs more control over the audio in- or output, ie. add a Tone-Generator or special
 * Analyser or Filter in the In- or Output-Path, you can create your own class, possibly extending
 * StandardClientPipeline and annotate it with <code>@Component @Primary</code> to override this default
 * implementation.
 */
@Slf4j
@Component
public class DefaultClientPipeline extends StandardClientPipeline {
	public DefaultClientPipeline(
		final List<AudioSystem> audioSystems
	) {
		super(audioSystems);
	}
}
