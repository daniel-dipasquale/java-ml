package com.dipasquale.common;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.Generated;
import lombok.NoArgsConstructor;

import javax.measure.quantity.Duration;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Generated
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class DateTimeConstants {
    public static final ZoneId UTC = ZoneId.of("UTC");

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
            .withZone(UTC);

    public static final DateTimeFormatter DATE_TIME_PARSER = DateTimeFormatter.ofPattern("yyyy-MM-dd[[ ]['T']HH:mm[:ss[.SSS][z][Z]]]")
            .withZone(UTC);

    public static final EnumMap<TimeUnit, Unit<Duration>> TIME_UNITS = createTimeUnits();
    public static final Map<Unit<Duration>, TimeUnit> UNITS = createUnits();
    public static final Unit<Duration> MILLISECONDS_UNIT = TIME_UNITS.get(TimeUnit.MILLISECONDS);
    public static final Unit<Duration> NANOSECONDS_UNIT = TIME_UNITS.get(TimeUnit.NANOSECONDS);

    private static EnumMap<TimeUnit, Unit<Duration>> createTimeUnits() {
        EnumMap<TimeUnit, Unit<Duration>> timeUnits = new EnumMap<>(TimeUnit.class);

        timeUnits.put(TimeUnit.NANOSECONDS, SI.NANO(SI.SECOND));
        timeUnits.put(TimeUnit.MICROSECONDS, SI.MICRO(SI.SECOND));
        timeUnits.put(TimeUnit.MILLISECONDS, SI.MILLI(SI.SECOND));
        timeUnits.put(TimeUnit.SECONDS, SI.SECOND);
        timeUnits.put(TimeUnit.MINUTES, NonSI.MINUTE);
        timeUnits.put(TimeUnit.HOURS, NonSI.HOUR);
        timeUnits.put(TimeUnit.DAYS, NonSI.DAY);

        return timeUnits;
    }

    private static Map<Unit<Duration>, TimeUnit> createUnits() {
        return ImmutableMap.<Unit<Duration>, TimeUnit>builder()
                .put(TIME_UNITS.get(TimeUnit.NANOSECONDS), TimeUnit.NANOSECONDS)
                .put(TIME_UNITS.get(TimeUnit.MICROSECONDS), TimeUnit.MICROSECONDS)
                .put(TIME_UNITS.get(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS)
                .put(TIME_UNITS.get(TimeUnit.SECONDS), TimeUnit.SECONDS)
                .put(TIME_UNITS.get(TimeUnit.MINUTES), TimeUnit.MINUTES)
                .put(TIME_UNITS.get(TimeUnit.HOURS), TimeUnit.HOURS)
                .put(TIME_UNITS.get(TimeUnit.DAYS), TimeUnit.DAYS)
                .build();
    }
}
