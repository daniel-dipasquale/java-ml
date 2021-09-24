package com.dipasquale.synchronization.wait.handle;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Generated
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@EqualsAndHashCode
@ToString
final class TimeUnitPair implements Comparable<TimeUnitPair> { // TODO: remove this class when the multi wait handle test case is reworked
    private static final Map<TimeUnit, Integer> WEIGHTS = ImmutableMap.<TimeUnit, Integer>builder()
            .put(TimeUnit.NANOSECONDS, 0)
            .put(TimeUnit.MICROSECONDS, 1)
            .put(TimeUnit.MILLISECONDS, 2)
            .put(TimeUnit.SECONDS, 3)
            .put(TimeUnit.MINUTES, 4)
            .put(TimeUnit.HOURS, 5)
            .put(TimeUnit.DAYS, 6)
            .build();

    private final long time;
    private final TimeUnit unit;

    @Override
    public int compareTo(final TimeUnitPair other) {
        int comparison = Integer.compare(WEIGHTS.get(unit), WEIGHTS.get(other.unit));

        if (comparison < 0) {
            return Long.compare(other.unit.convert(time, unit), other.time);
        }

        if (comparison == 0) {
            return Long.compare(time, other.time);
        }

        return Long.compare(time, unit.convert(other.time, other.unit));
    }

    public static TimeUnitPair min(final TimeUnitPair x, final TimeUnitPair y) {
        if (x.compareTo(y) <= 0) {
            return x;
        }

        return y;
    }

    public static TimeUnitPair max(final TimeUnitPair x, final TimeUnitPair y) {
        if (x.compareTo(y) >= 0) {
            return x;
        }

        return y;
    }
}
