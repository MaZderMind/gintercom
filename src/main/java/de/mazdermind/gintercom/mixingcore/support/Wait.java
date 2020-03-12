package de.mazdermind.gintercom.mixingcore.support;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Wait {

	private final Semaphore sem;

	public Wait() {
		sem = new Semaphore(0);
	}

	public void complete() {
		sem.release();
	}

	public void await() {
		try {
			if (!sem.tryAcquire(1, TimeUnit.MINUTES)) {
				throw new RuntimeException(new TimeoutException("Waiting timed out"));
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
