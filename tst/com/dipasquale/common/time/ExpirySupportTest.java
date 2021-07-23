package com.dipasquale.common.time;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.measure.unit.SI;
import java.util.concurrent.atomic.AtomicLong;

public final class ExpirySupportTest {
    private static final AtomicLong CURRENT_DATE_TIME = new AtomicLong();
    private static final DateTimeSupport DATE_TIME_SUPPORT = new DateTimeSupportProxy(CURRENT_DATE_TIME::get, SI.MILLI(SI.SECOND));

    private static ExpirySupport createExpirySupportSlider(final long expiryTime, final long expiryTimeSubtraction) {
        return ExpirySupport.createRounded(DATE_TIME_SUPPORT, expiryTime, expiryTimeSubtraction);
    }

    private static ExpirySupport createExpirySupportSlider(final long expiryTime) {
        return ExpirySupport.createRounded(DATE_TIME_SUPPORT, expiryTime);
    }

    private static ExpirySupport createExpirySupport(final long expiryTime, final long expiryTimeSubtraction) {
        return ExpirySupport.create(DATE_TIME_SUPPORT, expiryTime, expiryTimeSubtraction);
    }

    private static ExpirySupport createExpirySupport(final long expiryTime) {
        return ExpirySupport.create(DATE_TIME_SUPPORT, expiryTime);
    }

    @BeforeEach
    public void beforeEach() {
        CURRENT_DATE_TIME.set(0L);
    }

    @Test
    public void TEST_1() {
        ExpirySupport test_slider = createExpirySupportSlider(100L);
        ExpirySupport test = createExpirySupport(100L);

        CURRENT_DATE_TIME.set(1_049L);
        Assertions.assertEquals(new ExpiryRecord(1_049L, 1_100L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_049L, 1_100L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_050L);
        Assertions.assertEquals(new ExpiryRecord(1_050L, 1_200L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_050L, 1_100L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_051L);
        Assertions.assertEquals(new ExpiryRecord(1_051L, 1_200L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_051L, 1_100L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_099L);
        Assertions.assertEquals(new ExpiryRecord(1_099L, 1_200L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_099L, 1_100L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_100L);
        Assertions.assertEquals(new ExpiryRecord(1_100L, 1_200L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_100L, 1_200L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_101L);
        Assertions.assertEquals(new ExpiryRecord(1_101L, 1_200L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_101L, 1_200L, SI.MILLI(SI.SECOND)), test.next());
    }

    @Test
    public void TEST_2() {
        ExpirySupport test_slider = createExpirySupportSlider(100L, 50L);
        ExpirySupport test = createExpirySupport(100L, 50L);

        CURRENT_DATE_TIME.set(1_049L);
        Assertions.assertEquals(new ExpiryRecord(1_049L, 1_150L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_049L, 1_050L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_050L);
        Assertions.assertEquals(new ExpiryRecord(1_050L, 1_150L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_050L, 1_150L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_051L);
        Assertions.assertEquals(new ExpiryRecord(1_051L, 1_150L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_051L, 1_150L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_099L);
        Assertions.assertEquals(new ExpiryRecord(1_099L, 1_150L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_099L, 1_150L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_100L);
        Assertions.assertEquals(new ExpiryRecord(1_100L, 1_250L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_100L, 1_150L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_101L);
        Assertions.assertEquals(new ExpiryRecord(1_101L, 1_250L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_101L, 1_150L, SI.MILLI(SI.SECOND)), test.next());
    }

    @Test
    public void TEST_3() {
        ExpirySupport test_slider = createExpirySupportSlider(1L);
        ExpirySupport test = createExpirySupport(1L);

        CURRENT_DATE_TIME.set(1_049L);
        Assertions.assertEquals(new ExpiryRecord(1_049L, 1_050L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_049L, 1_050L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_050L);
        Assertions.assertEquals(new ExpiryRecord(1_050L, 1_051L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_050L, 1_051L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_051L);
        Assertions.assertEquals(new ExpiryRecord(1_051L, 1_052L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_051L, 1_052L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_099L);
        Assertions.assertEquals(new ExpiryRecord(1_099L, 1_100L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_099L, 1_100L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_100L);
        Assertions.assertEquals(new ExpiryRecord(1_100L, 1_101L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_100L, 1_101L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_101L);
        Assertions.assertEquals(new ExpiryRecord(1_101L, 1_102L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_101L, 1_102L, SI.MILLI(SI.SECOND)), test.next());
    }

    @Test
    public void TEST_4() {
        ExpirySupport test_slider = ExpirySupport.createRoundedFactory(DATE_TIME_SUPPORT).create(100L);
        ExpirySupport test = ExpirySupport.createFactory(DATE_TIME_SUPPORT).create(100L);

        CURRENT_DATE_TIME.set(1_049L);
        Assertions.assertEquals(new ExpiryRecord(1_049L, 1_100L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_049L, 1_100L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_050L);
        Assertions.assertEquals(new ExpiryRecord(1_050L, 1_200L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_050L, 1_100L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_051L);
        Assertions.assertEquals(new ExpiryRecord(1_051L, 1_200L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_051L, 1_100L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_099L);
        Assertions.assertEquals(new ExpiryRecord(1_099L, 1_200L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_099L, 1_100L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_100L);
        Assertions.assertEquals(new ExpiryRecord(1_100L, 1_200L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_100L, 1_200L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_101L);
        Assertions.assertEquals(new ExpiryRecord(1_101L, 1_200L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_101L, 1_200L, SI.MILLI(SI.SECOND)), test.next());
    }

    @Test
    public void TEST_5() {
        ExpirySupport test_slider = ExpirySupport.createRoundedFactory(DATE_TIME_SUPPORT).create(100L);
        ExpirySupport test = ExpirySupport.createFactory(DATE_TIME_SUPPORT).create(100L);

        CURRENT_DATE_TIME.set(1_049L);
        Assertions.assertEquals(new ExpiryRecord(1_049L, 1_100L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_049L, 1_100L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_050L);
        Assertions.assertEquals(new ExpiryRecord(1_050L, 1_200L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_050L, 1_100L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_051L);
        Assertions.assertEquals(new ExpiryRecord(1_051L, 1_200L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_051L, 1_100L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_099L);
        Assertions.assertEquals(new ExpiryRecord(1_099L, 1_200L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_099L, 1_100L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_100L);
        Assertions.assertEquals(new ExpiryRecord(1_100L, 1_200L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_100L, 1_200L, SI.MILLI(SI.SECOND)), test.next());
        CURRENT_DATE_TIME.set(1_101L);
        Assertions.assertEquals(new ExpiryRecord(1_101L, 1_200L, SI.MILLI(SI.SECOND)), test_slider.next());
        Assertions.assertEquals(new ExpiryRecord(1_101L, 1_200L, SI.MILLI(SI.SECOND)), test.next());
    }
}
