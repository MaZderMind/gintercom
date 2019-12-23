package de.mazdermind.gintercom.matrix.integration;

import org.springframework.boot.test.mock.mockito.MockBean;

import de.mazdermind.gintercom.matrix.pipeline.Pipeline;

public abstract class IntegrationWithoutGstreamerPipelineTestBase extends IntegrationTestBase {
	@MockBean
	private Pipeline pipeline;

	public Pipeline getPipelineMock() {
		return pipeline;
	}
}
