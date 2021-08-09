package com.dipasquale.threading.wait.handle;

import java.io.Serial;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public final class ReusableCountLatch implements WaitHandle {
    private final Synchronizer synchronizer;

    // NOTE: based on: https://github.com/MatejTymes/JavaFixes/blob/master/src/main/java/javafixes/concurrency/ReusableCountLatch.java
    public ReusableCountLatch(final int initialValue) {
        this.synchronizer = new Synchronizer(initialValue);
    }

    @Override
    public void await()
            throws InterruptedException {
        synchronizer.acquireSharedInterruptibly(1);
    }

    @Override
    public boolean await(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return synchronizer.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    public void countUp() {
        synchronizer.increment();
    }

    public void countDown() {
        synchronizer.decrement();
    }

    private static class Synchronizer extends AbstractQueuedSynchronizer {
        @Serial
        private static final long serialVersionUID = 4435118903758077543L;

        public Synchronizer(final int count) {
            setState(count);
        }

        public int getCount() {
            return getState();
        }

        public void increment() {
            for (int state = getState(); !compareAndSetState(state, state + 1); ) {
                state = getState();
            }
        }

        public void decrement() {
            releaseShared(1);
        }

        @Override
        protected int tryAcquireShared(final int acquires) {
            return getState() == 0 ? 1 : -1;
        }

        @Override
        protected boolean tryReleaseShared(final int releases) {
            while (true) {
                int state = getState();

                if (state == 0) {
                    return false;
                }

                int stateNew = state - 1;

                if (compareAndSetState(state, stateNew)) {
                    return stateNew == 0;
                }
            }
        }
    }
}
