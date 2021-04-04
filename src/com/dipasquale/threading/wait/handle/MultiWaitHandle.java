package com.dipasquale.threading.wait.handle;

import com.dipasquale.common.DateTimeSupport;
import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Duration;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public final class MultiWaitHandle {
    private static final Map<TimeUnitConversionKey, UnitConverter> TIME_UNIT_CONVERTERS = createTimeUnitConverters();
    private final DateTimeSupport dateTimeSupport;
    private final Predicate predicate;
    private final List<? extends WaitHandle> waitHandles;

    public MultiWaitHandle(final DateTimeSupport dateTimeSupport, final List<? extends WaitHandle> waitHandles) {
        this(dateTimeSupport, a -> a == 1, waitHandles);
    }

    private static Map<TimeUnitConversionKey, UnitConverter> createTimeUnitConverters() {
        List<TimeUnit> timeUnits = ImmutableList.<TimeUnit>builder()
                .add(TimeUnit.NANOSECONDS)
                .add(TimeUnit.MICROSECONDS)
                .add(TimeUnit.MILLISECONDS)
                .add(TimeUnit.SECONDS)
                .add(TimeUnit.MINUTES)
                .add(TimeUnit.HOURS)
                .add(TimeUnit.DAYS)
                .build();

        List<Unit<Duration>> units = ImmutableList.<Unit<Duration>>builder()
                .add(SI.NANO(SI.SECOND))
                .add(SI.MICRO(SI.SECOND))
                .add(SI.MILLI(SI.SECOND))
                .add(SI.SECOND)
                .add(NonSI.MINUTE)
                .add(NonSI.HOUR)
                .add(NonSI.DAY)
                .build();

        Map<TimeUnitConversionKey, UnitConverter> timeUnitConverters = new HashMap<>(timeUnits.size() * units.size());

        for (TimeUnit timeUnit : timeUnits) {
            Unit<Duration> unitFromTimeUnit = DateTimeSupport.getUnit(timeUnit);

            for (Unit<Duration> unit : units) {
                TimeUnitConversionKey conversionKey = new TimeUnitConversionKey(timeUnit, unit);

                timeUnitConverters.put(conversionKey, unitFromTimeUnit.getConverterTo(unit));
            }
        }

        return timeUnitConverters;
    }

    public void await()
            throws InterruptedException {
        for (int attempt = 0; predicate.shouldAwait(++attempt); ) {
            for (WaitHandle waitHandle : waitHandles) {
                waitHandle.await();
            }
        }
    }

    public boolean await(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        boolean acquired = true;
        long offsetDateTime = dateTimeSupport.now();
        TimeUnitConversionKey conversionKey = new TimeUnitConversionKey(unit, dateTimeSupport.unit());
        long timeoutRemaining = (long) TIME_UNIT_CONVERTERS.get(conversionKey).convert((double) timeout);
        TimeUnit timeUnit = dateTimeSupport.timeUnit();

        for (int attempt = 0; acquired && timeoutRemaining > 0L && predicate.shouldAwait(++attempt); ) {
            for (int i = 0, c = waitHandles.size(); i < c && acquired && timeoutRemaining > 0L; i++) {
                acquired = waitHandles.get(i).await(timeoutRemaining, timeUnit);

                if (acquired) {
                    long currentDateTime = dateTimeSupport.now();

                    timeoutRemaining -= currentDateTime - offsetDateTime;
                    offsetDateTime = currentDateTime;
                }
            }
        }

        return acquired;
    }

    @FunctionalInterface
    public interface Predicate {
        boolean shouldAwait(int attempt);
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    private static final class TimeUnitConversionKey {
        private final TimeUnit timeUnit;
        private final Unit<Duration> unit;
    }
}
