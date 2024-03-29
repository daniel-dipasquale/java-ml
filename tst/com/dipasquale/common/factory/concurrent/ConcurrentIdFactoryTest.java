package com.dipasquale.common.factory.concurrent;

import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.common.time.ExpirationFactory;
import com.dipasquale.common.time.ProxyDateTimeSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class ConcurrentIdFactoryTest {
    private static final AtomicLong CURRENT_DATE_TIME = new AtomicLong();
    private static final DateTimeSupport DATE_TIME_SUPPORT = new ProxyDateTimeSupport(CURRENT_DATE_TIME::get, TimeUnit.MILLISECONDS);
    private static final ExpirationFactory EXPIRY_SUPPORT = DATE_TIME_SUPPORT.createBucketExpirationFactory(1L);
    private static final AtomicLong CURRENT_MINOR_ID = new AtomicLong();

    @BeforeEach
    public void beforeEach() {
        CURRENT_DATE_TIME.set(0L);
        CURRENT_MINOR_ID.set(0L);
    }

    @Test
    public void TEST_1() {
        ConcurrentIdFactory test = new ConcurrentIdFactory(EXPIRY_SUPPORT, ConcurrentHashMap::new, CURRENT_MINOR_ID::get);

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
        ConcurrentIdFactory test = new ConcurrentIdFactory(EXPIRY_SUPPORT, ConcurrentHashMap::new);

        Assertions.assertEquals(String.format("1.%d.0", threadId), test.createId().toString());
        Assertions.assertEquals(String.format("1.%d.1", threadId), test.createId().toString());
        CURRENT_DATE_TIME.incrementAndGet();
        Assertions.assertEquals(String.format("2.%d.0", threadId), test.createId().toString());
        Assertions.assertEquals(String.format("2.%d.1", threadId), test.createId().toString());
    }
}
