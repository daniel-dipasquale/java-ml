package com.dipasquale.threading.wait.handle;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

// NOTE: based on: https://github.com/MatejTymes/JavaFixes/blob/master/src/main/java/javafixes/concurrency/ReusableCountLatch.java
public final class ReusableCountDownLatch implements WaitHandle {
    private final Sync sync;
    @Getter(AccessLevel.PACKAGE)
    private final UnitTest unitTest;

    public ReusableCountDownLatch(final int initialValue) {
        this.sync = new Sync(initialValue);
        this.unitTest = new UnitTest();
    }

    @Override
    public void await()
            throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    @Override
    public boolean await(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    public void countUp() {
        sync.increment();
    }

    public void countDown() {
        sync.decrement();
    }

    private static class Sync extends AbstractQueuedSynchronizer {
        @Serial
        private static final long serialVersionUID = 4435118903758077543L;

        public Sync(final int count) {
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

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    final class UnitTest {
        public int getCount() {
            return sync.getCount();
        }
    }
}
