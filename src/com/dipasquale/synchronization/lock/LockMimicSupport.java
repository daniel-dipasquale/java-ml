package com.dipasquale.synchronization.lock;

import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.common.time.NanosecondsDateTimeSupport;
import com.dipasquale.synchronization.wait.handle.InteractiveWaitHandle;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class LockMimicSupport {
    private static final DateTimeSupport DATE_TIME_SUPPORT = new NanosecondsDateTimeSupport();

    public static void lock(final boolean locked, final InteractiveWaitHandle waitHandle, final Lock lock, final InteractiveWaitHandle optionalWaitHandle)
            throws InterruptedException {
        for (boolean isNowLocked = locked; !isNowLocked; ) {
            waitHandle.await();

            if (optionalWaitHandle != null) {
                optionalWaitHandle.await();
            }

            isNowLocked = lock.tryLock();
        }

        waitHandle.countUp();
    }

    public static void lock(final boolean locked, final InteractiveWaitHandle waitHandle, final Lock lock)
            throws InterruptedException {
        lock(locked, waitHandle, lock, null);
    }

    public static boolean tryLock(final boolean locked, final InteractiveWaitHandle waitHandle) {
        if (locked) {
            waitHandle.countUp();

            return true;
        }

        return false;
    }

    public static boolean tryLock(final boolean locked, final long time, final TimeUnit unit, final InteractiveWaitHandle waitHandle, final Lock lock, final InteractiveWaitHandle optionalWaitHandle)
            throws InterruptedException {
        if (!locked) {
            TimeTracking timeTracking = new TimeTracking(time, unit);
            boolean isNowLocked;

            do {
                if (!waitHandle.await(timeTracking.remaining, DATE_TIME_SUPPORT.timeUnit())) {
                    return false;
                }

                timeTracking.update();

                if (optionalWaitHandle != null) {
                    if (!optionalWaitHandle.await(timeTracking.remaining, DATE_TIME_SUPPORT.timeUnit())) {
                        return false;
                    }

                    timeTracking.update();
                }

                isNowLocked = lock.tryLock();
            } while (!isNowLocked);
        }

        waitHandle.countUp();

        return true;
    }

    public static boolean tryLock(final boolean locked, final long time, final TimeUnit unit, final InteractiveWaitHandle waitHandle, final Lock lock)
            throws InterruptedException {
        return tryLock(locked, time, unit, waitHandle, lock, null);
    }

    public static void unlock(final InteractiveWaitHandle waitHandle, final Lock lock) {
        waitHandle.countDown();
        lock.unlock();
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class TimeTracking {
        private long remaining;
        private long offsetDateTime;

        private TimeTracking(final long time, final TimeUnit unit) {
            this.remaining = DATE_TIME_SUPPORT.timeUnit().convert(time, unit);
            this.offsetDateTime = DATE_TIME_SUPPORT.now();
        }

        private void update() {
            long currentDateTime = DATE_TIME_SUPPORT.now();

            remaining -= currentDateTime - offsetDateTime;
            offsetDateTime = currentDateTime;
        }
    }
}
