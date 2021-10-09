package com.dipasquale.common.time;

import lombok.Generated;

import javax.measure.quantity.Duration;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

final class Constants {
    private static final ZoneId UTC = ZoneId.of("UTC");
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").withZone(UTC);
    static final DateTimeFormatter DATE_TIME_PARSER = DateTimeFormatter.ofPattern("yyyy-MM-dd[[ ]['T']HH:mm[:ss[.SSS][z][Z]]]").withZone(UTC);
    static final EnumMap<TimeUnit, Unit<Duration>> TIME_UNITS = createTimeUnits();
    static final Map<Unit<Duration>, TimeUnit> UNITS = createUnits();
    static final Unit<Duration> MILLISECONDS_UNIT = TIME_UNITS.get(TimeUnit.MILLISECONDS);
    static final Unit<Duration> NANOSECONDS_UNIT = TIME_UNITS.get(TimeUnit.NANOSECONDS);

    @Generated
    private Constants() {
    }

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
        return Map.ofEntries(
                Map.entry(SI.NANO(SI.SECOND), TimeUnit.NANOSECONDS),
                Map.entry(SI.MICRO(SI.SECOND), TimeUnit.MICROSECONDS),
                Map.entry(SI.MILLI(SI.SECOND), TimeUnit.MILLISECONDS),
                Map.entry(SI.SECOND, TimeUnit.SECONDS),
                Map.entry(NonSI.MINUTE, TimeUnit.MINUTES),
                Map.entry(NonSI.HOUR, TimeUnit.HOURS),
                Map.entry(NonSI.DAY, TimeUnit.DAYS)
        );
    }
}
