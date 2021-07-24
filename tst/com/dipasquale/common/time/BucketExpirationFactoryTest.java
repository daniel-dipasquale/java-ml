package com.dipasquale.common.time;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.measure.unit.SI;
import java.util.concurrent.atomic.AtomicLong;

public final class BucketExpirationFactoryTest {
    private static final AtomicLong CURRENT_DATE_TIME = new AtomicLong();
    private static final DateTimeSupport DATE_TIME_SUPPORT = new ProxyDateTimeSupport(CURRENT_DATE_TIME::get, SI.MILLI(SI.SECOND));

    private static ExpirationFactory createExpirationRoundedBucketFactory(final long bucketSize, final long bucketOffset) {
        return DATE_TIME_SUPPORT.createRoundedBucketExpirationFactory(bucketSize, bucketOffset);
    }

    private static ExpirationFactory createExpirationRoundedBucketFactory(final long bucketSize) {
        return DATE_TIME_SUPPORT.createRoundedBucketExpirationFactory(bucketSize);
    }

    private static ExpirationFactory createExpirationBucketFactory(final long bucketSize, final long bucketOffset) {
        return DATE_TIME_SUPPORT.createBucketExpirationFactory(bucketSize, bucketOffset);
    }

    private static ExpirationFactory createExpirationBucketFactory(final long bucketSize) {
        return DATE_TIME_SUPPORT.createBucketExpirationFactory(bucketSize);
    }

    @BeforeEach
    public void beforeEach() {
        CURRENT_DATE_TIME.set(0L);
    }

    @Test
    public void GIVEN_a_bucket_expiration_factory_with_an_explicit_bucket_size_and_sometimes_a_bucket_offset_WHEN_creating_the_expiration_record_relative_to_the_current_date_time_THEN_provide_the_current_date_time_and_the_date_time_the_record_is_set_to_expire() {
        ExpirationFactory test1 = createExpirationRoundedBucketFactory(100L);
        ExpirationFactory test2 = createExpirationBucketFactory(100L);
        ExpirationFactory test3 = createExpirationRoundedBucketFactory(100L, 50L);
        ExpirationFactory test4 = createExpirationBucketFactory(100L, 50L);

        CURRENT_DATE_TIME.set(1_049L);
        Assertions.assertEquals(new ExpirationRecord(1_049L, 1_100L, SI.MILLI(SI.SECOND)), test1.create());
        Assertions.assertEquals(new ExpirationRecord(1_049L, 1_100L, SI.MILLI(SI.SECOND)), test2.create());
        Assertions.assertEquals(new ExpirationRecord(1_049L, 1_150L, SI.MILLI(SI.SECOND)), test3.create());
        Assertions.assertEquals(new ExpirationRecord(1_049L, 1_050L, SI.MILLI(SI.SECOND)), test4.create());
        CURRENT_DATE_TIME.set(1_050L);
        Assertions.assertEquals(new ExpirationRecord(1_050L, 1_200L, SI.MILLI(SI.SECOND)), test1.create());
        Assertions.assertEquals(new ExpirationRecord(1_050L, 1_100L, SI.MILLI(SI.SECOND)), test2.create());
        Assertions.assertEquals(new ExpirationRecord(1_050L, 1_150L, SI.MILLI(SI.SECOND)), test3.create());
        Assertions.assertEquals(new ExpirationRecord(1_050L, 1_150L, SI.MILLI(SI.SECOND)), test4.create());
        CURRENT_DATE_TIME.set(1_051L);
        Assertions.assertEquals(new ExpirationRecord(1_051L, 1_200L, SI.MILLI(SI.SECOND)), test1.create());
        Assertions.assertEquals(new ExpirationRecord(1_051L, 1_100L, SI.MILLI(SI.SECOND)), test2.create());
        Assertions.assertEquals(new ExpirationRecord(1_051L, 1_150L, SI.MILLI(SI.SECOND)), test3.create());
        Assertions.assertEquals(new ExpirationRecord(1_051L, 1_150L, SI.MILLI(SI.SECOND)), test4.create());
        CURRENT_DATE_TIME.set(1_099L);
        Assertions.assertEquals(new ExpirationRecord(1_099L, 1_200L, SI.MILLI(SI.SECOND)), test1.create());
        Assertions.assertEquals(new ExpirationRecord(1_099L, 1_100L, SI.MILLI(SI.SECOND)), test2.create());
        Assertions.assertEquals(new ExpirationRecord(1_099L, 1_150L, SI.MILLI(SI.SECOND)), test3.create());
        Assertions.assertEquals(new ExpirationRecord(1_099L, 1_150L, SI.MILLI(SI.SECOND)), test4.create());
        CURRENT_DATE_TIME.set(1_100L);
        Assertions.assertEquals(new ExpirationRecord(1_100L, 1_200L, SI.MILLI(SI.SECOND)), test1.create());
        Assertions.assertEquals(new ExpirationRecord(1_100L, 1_200L, SI.MILLI(SI.SECOND)), test2.create());
        Assertions.assertEquals(new ExpirationRecord(1_100L, 1_250L, SI.MILLI(SI.SECOND)), test3.create());
        Assertions.assertEquals(new ExpirationRecord(1_100L, 1_150L, SI.MILLI(SI.SECOND)), test4.create());
        CURRENT_DATE_TIME.set(1_101L);
        Assertions.assertEquals(new ExpirationRecord(1_101L, 1_200L, SI.MILLI(SI.SECOND)), test1.create());
        Assertions.assertEquals(new ExpirationRecord(1_101L, 1_200L, SI.MILLI(SI.SECOND)), test2.create());
        Assertions.assertEquals(new ExpirationRecord(1_101L, 1_250L, SI.MILLI(SI.SECOND)), test3.create());
        Assertions.assertEquals(new ExpirationRecord(1_101L, 1_150L, SI.MILLI(SI.SECOND)), test4.create());
    }

    @Test
    public void GIVEN_a_bucket_expiration_factory_with_an_explicit_and_the_minimum_bucket_size_WHEN_creating_the_expiration_record_relative_to_the_current_date_time_THEN_provide_the_current_date_time_and_the_date_time_the_record_is_set_to_expire() {
        ExpirationFactory test1 = createExpirationRoundedBucketFactory(1L);
        ExpirationFactory test2 = createExpirationBucketFactory(1L);

        CURRENT_DATE_TIME.set(1_049L);
        Assertions.assertEquals(new ExpirationRecord(1_049L, 1_050L, SI.MILLI(SI.SECOND)), test1.create());
        Assertions.assertEquals(new ExpirationRecord(1_049L, 1_050L, SI.MILLI(SI.SECOND)), test2.create());
        CURRENT_DATE_TIME.set(1_050L);
        Assertions.assertEquals(new ExpirationRecord(1_050L, 1_051L, SI.MILLI(SI.SECOND)), test1.create());
        Assertions.assertEquals(new ExpirationRecord(1_050L, 1_051L, SI.MILLI(SI.SECOND)), test2.create());
        CURRENT_DATE_TIME.set(1_051L);
        Assertions.assertEquals(new ExpirationRecord(1_051L, 1_052L, SI.MILLI(SI.SECOND)), test1.create());
        Assertions.assertEquals(new ExpirationRecord(1_051L, 1_052L, SI.MILLI(SI.SECOND)), test2.create());
        CURRENT_DATE_TIME.set(1_099L);
        Assertions.assertEquals(new ExpirationRecord(1_099L, 1_100L, SI.MILLI(SI.SECOND)), test1.create());
        Assertions.assertEquals(new ExpirationRecord(1_099L, 1_100L, SI.MILLI(SI.SECOND)), test2.create());
        CURRENT_DATE_TIME.set(1_100L);
        Assertions.assertEquals(new ExpirationRecord(1_100L, 1_101L, SI.MILLI(SI.SECOND)), test1.create());
        Assertions.assertEquals(new ExpirationRecord(1_100L, 1_101L, SI.MILLI(SI.SECOND)), test2.create());
        CURRENT_DATE_TIME.set(1_101L);
        Assertions.assertEquals(new ExpirationRecord(1_101L, 1_102L, SI.MILLI(SI.SECOND)), test1.create());
        Assertions.assertEquals(new ExpirationRecord(1_101L, 1_102L, SI.MILLI(SI.SECOND)), test2.create());
    }
}
