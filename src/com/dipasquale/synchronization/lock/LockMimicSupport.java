package com.dipasquale.synchronization.lock;

import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.common.time.NanosecondsDateTimeSupport;
import com.dipasquale.synchronization.wait.handle.InteractiveWaitHandle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class LockMimicSupport {
    private static final DateTimeSupport DATE_TIME_SUPPORT = new NanosecondsDateTimeSupport();

    public static void lock(final boolean locked, final InteractiveWaitHandle waitHandle, final Lock lock)
            throws InterruptedException {
        for (boolean isNowLocked = locked; !isNowLocked; ) {
            waitHandle.await();
            isNowLocked = lock.tryLock();
        }

        waitHandle.countUp();
    }

    public static boolean tryLock(final boolean locked, final InteractiveWaitHandle waitHandle) {
        if (locked) {
            waitHandle.countUp();

            return true;
        }

        return false;
    }

    public static boolean tryLock(final boolean locked, final long time, final TimeUnit unit, final InteractiveWaitHandle waitHandle, final Lock lock)
            throws InterruptedException {
        if (!locked) {
            long offsetDateTime = DATE_TIME_SUPPORT.now();
            long timeRemaining = DATE_TIME_SUPPORT.timeUnit().convert(time, unit);
            boolean isNowLocked;

            do {
                if (!waitHandle.await(timeRemaining, DATE_TIME_SUPPORT.timeUnit())) {
                    return false;
                }

                long currentDateTime = DATE_TIME_SUPPORT.now();

                timeRemaining -= currentDateTime - offsetDateTime;
                offsetDateTime = currentDateTime;
                isNowLocked = lock.tryLock();
            } while (!isNowLocked);
        }

        waitHandle.countUp();

        return true;
    }
}
