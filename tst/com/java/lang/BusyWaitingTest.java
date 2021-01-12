package com.java.lang;

import com.dipasquale.common.DateTimeSupport;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public final class BusyWaitingTest {
    private static final DateTimeSupport DATE_TIME_SUPPORT = DateTimeSupport.createMilliseconds();
    private static final long DURATION = 60_000L;

    @Test
    @Ignore
    public void TEST_1() {
        long startDateTime = DATE_TIME_SUPPORT.now();

        while (DATE_TIME_SUPPORT.now() - startDateTime < DURATION) ;
    }

    @Test
    @Ignore
    public void TEST_2()
            throws InterruptedException {
        new CountDownLatch(1).await(DURATION, DATE_TIME_SUPPORT.timeUnit());
    }
}
