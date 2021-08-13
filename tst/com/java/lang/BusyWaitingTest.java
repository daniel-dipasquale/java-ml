/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.java.lang;

import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.common.time.MillisecondsDateTimeSupport;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

public final class BusyWaitingTest {
    private static final DateTimeSupport DATE_TIME_SUPPORT = new MillisecondsDateTimeSupport();
    private static final long DURATION = 60_000L;

    @Test
    @Disabled
    public void TEST_1() {
        long startDateTime = DATE_TIME_SUPPORT.now();

        while (DATE_TIME_SUPPORT.now() - startDateTime < DURATION) ;
    }

    @Test
    @Disabled
    public void TEST_2()
            throws InterruptedException {
        new CountDownLatch(1).await(DURATION, DATE_TIME_SUPPORT.timeUnit());
    }
}
