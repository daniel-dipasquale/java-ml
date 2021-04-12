package com.dipasquale.concurrent;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.ExpirySupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.measure.unit.SI;
import java.util.concurrent.atomic.AtomicLong;

public final class ConcurrentIdFactoryTest {
    private static final AtomicLong CURRENT_DATE_TIME = new AtomicLong();
    private static final DateTimeSupport DATE_TIME_SUPPORT = DateTimeSupport.create(CURRENT_DATE_TIME::get, SI.MILLI(SI.SECOND));
    private static final ExpirySupport EXPIRY_SUPPORT = ExpirySupport.create(DATE_TIME_SUPPORT, 1L);
    private static final AtomicLong CURRENT_MINOR_ID = new AtomicLong();

    @BeforeEach
    public void beforeEach() {
        CURRENT_DATE_TIME.set(0L);
        CURRENT_MINOR_ID.set(0L);
    }

    @Test
    public void TEST_1() {
        ConcurrentIdFactory test = new ConcurrentIdFactory(EXPIRY_SUPPORT, CURRENT_MINOR_ID::get, 16, 0.75f, 1);

        Assertions.assertEquals("1.0.0", test.createId().toString());
        Assertions.assertEquals("1.0.1", test.createId().toString());
        CURRENT_DATE_TIME.incrementAndGet();
        Assertions.assertEquals("2.0.0", test.createId().toString());
        Assertions.assertEquals("2.0.1", test.createId().toString());
        CURRENT_MINOR_ID.incrementAndGet();
        Assertions.assertEquals("2.1.0", test.createId().toString());
        Assertions.assertEquals("2.1.1", test.createId().toString());
    }

    @Test
    public void TEST_2() {
        long threadId = Thread.currentThread().getId();
        ConcurrentIdFactory test = new ConcurrentIdFactory(EXPIRY_SUPPORT, 16, 0.75f, 1);

        Assertions.assertEquals(String.format("1.%d.0", threadId), test.createId().toString());
        Assertions.assertEquals(String.format("1.%d.1", threadId), test.createId().toString());
        CURRENT_DATE_TIME.incrementAndGet();
        Assertions.assertEquals(String.format("2.%d.0", threadId), test.createId().toString());
        Assertions.assertEquals(String.format("2.%d.1", threadId), test.createId().toString());
    }
}
