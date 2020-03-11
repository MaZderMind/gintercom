package de.mazdermind.gintercom.mixingcore.support;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

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
			sem.tryAcquire(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
