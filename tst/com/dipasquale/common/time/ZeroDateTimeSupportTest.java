package com.dipasquale.common.time;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.measure.quantity.Duration;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import java.time.format.DateTimeFormatter;

public final class ZeroDateTimeSupportTest {
    private static final Unit<Duration> UNIT = SI.SECOND;
    private static final ZeroDateTimeSupport TEST = new ZeroDateTimeSupport(UNIT);

    @Test
    public void GIVEN_a_zero_date_time_support_WHEN_doing_minimal_testing_THEN_check_if_it_extends_abstract_date_time_support() {
        Assertions.assertTrue(TEST instanceof AbstractDateTimeSupport);
    }

    @Test
    public void GIVEN_a_zero_date_time_support_WHEN_getting_the_current_date_time_THEN_provide_it_in_epoch_format() {
        Assertions.assertEquals(0L, TEST.now());
    }

    @Test
    public void GIVEN_a_zero_date_time_support_WHEN_getting_the_unit_measuring_the_current_date_time_THEN_provide_it() {
        Assertions.assertEquals(SI.SECOND, TEST.unit());
    }

    @Test
    public void GIVEN_two_instances_of_zero_date_time_support_WHEN_comparing_whether_they_are_equal_THEN_indicate_they_are_equal_if_they_were_initialized_the_same_way_otherwise_indicate_they_are_not() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM");

        Assertions.assertEquals(new ZeroDateTimeSupport(UNIT), TEST);
        Assertions.assertNotEquals(new ZeroDateTimeSupport(UNIT, formatter, parser), TEST);
        Assertions.assertEquals(new ZeroDateTimeSupport(UNIT, Constants.DATE_TIME_FORMATTER, Constants.DATE_TIME_PARSER), TEST);
    }
}
