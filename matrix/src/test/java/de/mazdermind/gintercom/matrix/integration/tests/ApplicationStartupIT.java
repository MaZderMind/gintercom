package de.mazdermind.gintercom.matrix.integration.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.freedesktop.gstreamer.State;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.matrix.integration.IntegrationTestBase;
import de.mazdermind.gintercom.matrix.pipeline.Pipeline;

public class ApplicationStartupIT extends IntegrationTestBase {
	@Autowired
	private Pipeline pipeline;

	@Test
	public void pipelineStartsUp() {
		assertThat(pipeline.getState(), is(State.PLAYING));
	}
}
