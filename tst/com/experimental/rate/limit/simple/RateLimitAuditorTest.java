package com.experimental.rate.limit.simple;

import com.experimental.rate.limit.RateLimitChecker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class RateLimitAuditorTest {
    private static RateLimitAuditor createAuditor(final int limit, final int buckets) {
        RateLimitSlidingWindow slidingWindow = RateLimitSlidingWindow.builder()
                .milliseconds(1L)
                .buckets(buckets)
                .build();

        RateLimitChecker checker = new RateLimitCheckerDefault(limit);

        return new RateLimitAuditor(slidingWindow, checker);
    }

    @Test
    public void GIVEN_a_rate_limit_auditor_that_caps_3_transactions_per_millisecond_WHEN_checking_if_the_limit_was_hit_THEN_do_not_fail_until_the_limit_is_hit() {
        RateLimitAuditor auditor = createAuditor(3, 1);

        Assertions.assertFalse(auditor.isLimitHit(1L, 1));
        Assertions.assertFalse(auditor.isLimitHit(1L, 1));
        Assertions.assertTrue(auditor.isLimitHit(1L, 1));
        Assertions.assertTrue(auditor.isLimitHit(1L, 1));
    }

    @Test
    public void GIVEN_a_rate_limit_auditor_that_caps_3_transactions_per_3_milliseconds_WHEN_checking_if_the_limit_was_hit_THEN_do_not_fail_until_the_limit_is_hit() {
        RateLimitAuditor auditor = createAuditor(3, 3);

        Assertions.assertFalse(auditor.isLimitHit(1L, 1));
        Assertions.assertFalse(auditor.isLimitHit(2L, 1));
        Assertions.assertTrue(auditor.isLimitHit(3L, 1));
        Assertions.assertTrue(auditor.isLimitHit(3L, 1));
    }

    @Test
    public void GIVEN_a_rate_limit_auditor_that_caps_3_transactions_per_3_milliseconds_WHEN_checking_if_the_limit_was_hit_THEN_do_not_fail_until_the_limit_is_hit_but_also_slide_the_window_when_time_passes_and_clear_the_buckets() {
        RateLimitAuditor auditor = createAuditor(3, 3);

        Assertions.assertFalse(auditor.isLimitHit(1L, 1));
        Assertions.assertFalse(auditor.isLimitHit(1L, 1));
        Assertions.assertTrue(auditor.isLimitHit(3L, 1));
        Assertions.assertFalse(auditor.isLimitHit(4L, 1));
        Assertions.assertTrue(auditor.isLimitHit(5L, 1));
        Assertions.assertTrue(auditor.isLimitHit(6L, 1));
    }

    @Test
    public void GIVEN_a_rate_limit_auditor_that_caps_1_transaction_per_millisecond_WHEN_checking_if_the_limit_was_hit_THEN_fail_every_time_the_limit_is_queried() {
        RateLimitAuditor auditor = createAuditor(1, 1);

        Assertions.assertTrue(auditor.isLimitHit(1L, 1));
        Assertions.assertTrue(auditor.isLimitHit(1L, 1));
    }

    @Test
    public void TEST_6() {
        RateLimitAuditor auditor = createAuditor(3, 3);

        Assertions.assertEquals(0, auditor.getWaitTime(1L));
        Assertions.assertFalse(auditor.isLimitHit(1L, 1));
        Assertions.assertEquals(0, auditor.getWaitTime(1L));
        Assertions.assertFalse(auditor.isLimitHit(2L, 1));
        Assertions.assertEquals(0, auditor.getWaitTime(2L));
        Assertions.assertTrue(auditor.isLimitHit(3L, 1));
        Assertions.assertEquals(1, auditor.getWaitTime(3L));
    }

    @Test
    public void TEST_7() {
        RateLimitAuditor auditor = createAuditor(3, 3);

        Assertions.assertEquals(0, auditor.getWaitTime(1L));
        Assertions.assertFalse(auditor.isLimitHit(1L, 1));
        Assertions.assertEquals(0, auditor.getWaitTime(1L));
        Assertions.assertFalse(auditor.isLimitHit(1L, 1));
        Assertions.assertEquals(0, auditor.getWaitTime(2L));
        Assertions.assertTrue(auditor.isLimitHit(3L, 1));
        Assertions.assertEquals(1, auditor.getWaitTime(3L));
    }

    @Test
    public void TEST_8() {
        RateLimitAuditor auditor = createAuditor(3, 3);

        Assertions.assertEquals(0, auditor.getWaitTime(1L));
        Assertions.assertFalse(auditor.isLimitHit(1L, 1));
        Assertions.assertEquals(0, auditor.getWaitTime(1L));
        Assertions.assertFalse(auditor.isLimitHit(1L, 1));
        Assertions.assertEquals(0, auditor.getWaitTime(1L));
        Assertions.assertTrue(auditor.isLimitHit(1L, 1));
        Assertions.assertEquals(3, auditor.getWaitTime(1L));
        Assertions.assertEquals(2, auditor.getWaitTime(2L));
        Assertions.assertEquals(1, auditor.getWaitTime(3L));
        Assertions.assertEquals(0, auditor.getWaitTime(4L));
    }

    @Test
    public void TEST_9() {
        RateLimitAuditor auditor = createAuditor(3, 3);

        Assertions.assertTrue(auditor.cleared(1L));
        Assertions.assertFalse(auditor.isLimitHit(1L, 1));
        Assertions.assertFalse(auditor.cleared(1L));
        Assertions.assertFalse(auditor.isLimitHit(2L, 1));
        Assertions.assertFalse(auditor.cleared(2L));
        Assertions.assertTrue(auditor.isLimitHit(3L, 1));
        Assertions.assertFalse(auditor.cleared(3L));
        Assertions.assertFalse(auditor.cleared(4L));
        Assertions.assertFalse(auditor.cleared(5L));
        Assertions.assertTrue(auditor.cleared(6L));
    }
}
