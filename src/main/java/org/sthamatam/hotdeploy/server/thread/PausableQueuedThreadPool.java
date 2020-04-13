package org.sthamatam.hotdeploy.server.thread;

import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author sunilthamatam
 */
public class PausableQueuedThreadPool extends QueuedThreadPool {

	private boolean isPaused;
	private ReentrantLock pauseLock = new ReentrantLock();
	private Condition unpaused = pauseLock.newCondition();


	@Override
	public void execute(Runnable job) {
		pauseLock.lock();
		try {
			while (isPaused) unpaused.await();

			super.execute(job);

		} catch (InterruptedException ie) {
			throw new RuntimeException(ie);
		} finally {
			pauseLock.unlock();
		}
	}

	public void pause() {
		pauseLock.lock();
		try {
			isPaused = true;
		} finally {
			pauseLock.unlock();
		}
	}

	public void resume() {
		pauseLock.lock();
		try {
			isPaused = false;
			unpaused.signalAll();
		} finally {
			pauseLock.unlock();
		}
	}

}
