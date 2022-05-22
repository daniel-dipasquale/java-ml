package com.dipasquale.synchronization.wait.handle;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public final class ReusableCountDownWaitHandle implements InteractiveWaitHandle, Serializable {
    @Serial
    private static final long serialVersionUID = 4359959667013992563L;
    private final Synchronizer synchronizer;

    public ReusableCountDownWaitHandle(final int initialValue, final WaitCondition waitCondition) { // NOTE: based on: https://github.com/MatejTymes/JavaFixes/blob/master/src/main/java/javafixes/concurrency/ReusableCountLatch.java
        this.synchronizer = new Synchronizer(initialValue, waitCondition);
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

    @Override
    public boolean countUp() {
        synchronizer.increment();

        return true;
    }

    @Override
    public boolean countDown() {
        synchronizer.decrement();

        return true;
    }

    private static class Synchronizer extends AbstractQueuedSynchronizer {
        @Serial
        private static final long serialVersionUID = 4435118903758077543L;
        private final WaitCondition waitCondition;

        private Synchronizer(final int state, final WaitCondition waitCondition) {
            setState(state);
            this.waitCondition = waitCondition;
        }

        public void increment() {
            int state = getState();

            while (!compareAndSetState(state, state + 1)) {
                state = getState();
            }
        }

        public void decrement() {
            releaseShared(1);
        }

        @Override
        protected int tryAcquireShared(final int acquires) {
            int state = getState();

            return switch (waitCondition) {
                case ON_NOT_ZERO -> state == 0 ? 1 : -1;

                case ON_ZERO -> state != 0 ? 1 : -1;
            };
        }

        @Override
        protected boolean tryReleaseShared(final int releases) {
            while (true) {
                int state = getState();

                if (state == 0 && waitCondition == WaitCondition.ON_NOT_ZERO || state != 0 && waitCondition == WaitCondition.ON_ZERO) {
                    return false;
                }

                int fixedState = state - 1;

                if (compareAndSetState(state, fixedState)) {
                    return switch (waitCondition) {
                        case ON_NOT_ZERO -> fixedState == 0;

                        case ON_ZERO -> true;
                    };
                }
            }
        }
    }
}
