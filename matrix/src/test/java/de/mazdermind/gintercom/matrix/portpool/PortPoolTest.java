package de.mazdermind.gintercom.matrix.portpool;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class PortPoolTest {
	private PortPool portPool;

	@Before
	public void prepare() {
		portPool = new PortPool(5000, 3);
	}

	@Test
	public void allocatesSequentialPorts() {
		assertThat(portPool.getNextPort(), is(5000));
		assertThat(portPool.getNextPort(), is(5001));
		assertThat(portPool.getNextPort(), is(5002));
	}

	@Test(expected = PortPool.PoolExhaustedException.class)
	public void exhaustsAfterTheGivenNumberOfPorts() {
		portPool.getNextPort();
		portPool.getNextPort();
		portPool.getNextPort();
		portPool.getNextPort();
	}
}
