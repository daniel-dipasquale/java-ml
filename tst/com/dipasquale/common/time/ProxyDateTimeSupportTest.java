package com.dipasquale.common.time;

import com.dipasquale.common.factory.LongFactory;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Serial;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class ProxyDateTimeSupportTest {
    private static final AtomicLong CURRENT_DATE_TIME = new AtomicLong();
    private static final LongFactoryMock NOW_FACTORY = new LongFactoryMock(CURRENT_DATE_TIME);
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static final ProxyDateTimeSupport TEST = new ProxyDateTimeSupport(NOW_FACTORY, TIME_UNIT);

    @BeforeEach
    public void beforeEach() {
        CURRENT_DATE_TIME.set(0L);
    }

    @Test
    public void GIVEN_a_proxy_date_time_support_WHEN_getting_the_current_date_time_THEN_provide_it_in_epoch_format() {
        Assertions.assertEquals(1L, TEST.now());
    }

    @Test
    public void GIVEN_a_proxy_date_time_support_WHEN_getting_the_unit_measuring_the_current_date_time_THEN_provide_it() {
        Assertions.assertEquals(TimeUnit.SECONDS, TEST.timeUnit());
    }

    @Test
    public void GIVEN_two_instances_of_proxy_date_time_support_WHEN_comparing_whether_they_are_equal_THEN_indicate_they_are_equal_if_they_were_initialized_the_same_way_otherwise_indicate_they_are_not() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM");

        Assertions.assertEquals(new ProxyDateTimeSupport(NOW_FACTORY, TIME_UNIT), TEST);
        Assertions.assertNotEquals(new ProxyDateTimeSupport(NOW_FACTORY, TIME_UNIT, formatter, parser), TEST);
        Assertions.assertEquals(new ProxyDateTimeSupport(NOW_FACTORY, TIME_UNIT, Constants.DATE_TIME_FORMATTER, Constants.DATE_TIME_PARSER), TEST);
        Assertions.assertNotEquals(new ProxyDateTimeSupport(() -> 0L, TIME_UNIT), TEST);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    private static final class LongFactoryMock implements LongFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 344354311071266964L;
        private final AtomicLong nowCas;
        @EqualsAndHashCode.Include
        private long now = 0L;

        @Override
        public long create() {
            return now = nowCas.incrementAndGet();
        }
    }
}
