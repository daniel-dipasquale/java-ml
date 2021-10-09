package com.dipasquale.synchronization.wait.handle;

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
final class TimeUnitPair implements Comparable<TimeUnitPair> {
    private static final Map<TimeUnit, Integer> WEIGHTS = Map.ofEntries(
            Map.entry(TimeUnit.NANOSECONDS, 0),
            Map.entry(TimeUnit.MICROSECONDS, 1),
            Map.entry(TimeUnit.MILLISECONDS, 2),
            Map.entry(TimeUnit.SECONDS, 3),
            Map.entry(TimeUnit.MINUTES, 4),
            Map.entry(TimeUnit.HOURS, 5),
            Map.entry(TimeUnit.DAYS, 6)
    );

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
