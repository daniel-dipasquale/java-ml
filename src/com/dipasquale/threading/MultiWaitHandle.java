package com.dipasquale.threading;

import com.dipasquale.common.DateTimeSupport;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
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

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MultiWaitHandle {
    private static final Map<TimeUnitConversionKey, UnitConverter> TIME_UNIT_CONVERTERS = createTimeUnitConverters();
    private final DateTimeSupport dateTimeSupport;
    private final HandlerInvocationPredicate handlerInvocationPredicate;
    private final List<?> waitHandles;
    private final IndefiniteHandler<?> indefinite;
    private final TimedHandler<?> timedHandler;

    private static Map<TimeUnitConversionKey, UnitConverter> createTimeUnitConverters() {
        List<TimeUnit> timeUnits = ImmutableList.<TimeUnit>builder()
                .add(TimeUnit.NANOSECONDS)
                .add(TimeUnit.MICROSECONDS)
                .add(TimeUnit.MILLISECONDS)
                .add(TimeUnit.SECONDS)
                .add(TimeUnit.MINUTES)
                .add(TimeUnit.HOURS)
                .build();

        List<Unit<Duration>> units = ImmutableList.<Unit<Duration>>builder()
                .add(SI.NANO(SI.SECOND))
                .add(SI.MICRO(SI.SECOND))
                .add(SI.MILLI(SI.SECOND))
                .add(SI.SECOND)
                .add(NonSI.MINUTE)
                .add(NonSI.HOUR)
                .build();

        Map<TimeUnitConversionKey, UnitConverter> timeUnitConverters = new HashMap<>(timeUnits.size() * units.size());

        for (TimeUnit timeUnit : timeUnits) {
            Unit<Duration> timeUnitConverted = DateTimeSupport.getUnit(timeUnit);

            for (Unit<Duration> unit : units) {
                TimeUnitConversionKey timeUnitConversionKey = new TimeUnitConversionKey(timeUnit, unit);

                timeUnitConverters.put(timeUnitConversionKey, timeUnitConverted.getConverterTo(unit));
            }
        }

        return timeUnitConverters;
    }

    public static <T> MultiWaitHandle create(final DateTimeSupport dateTimeSupport, final HandlerInvocationPredicate handlerInvocationPredicate, final List<T> waitHandles, final IndefiniteHandler<T> indefinite, final TimedHandler<T> timedHandler) {
        return new MultiWaitHandle(dateTimeSupport, handlerInvocationPredicate, waitHandles, indefinite, timedHandler);
    }

    public static <T> MultiWaitHandle createSinglePass(final DateTimeSupport dateTimeSupport, final List<T> waitHandles, final IndefiniteHandler<T> indefinite, final TimedHandler<T> timedHandler) {
        HandlerInvocationPredicate handlerInvocationPredicate = a -> a == 1;

        return create(dateTimeSupport, handlerInvocationPredicate, waitHandles, indefinite, timedHandler);
    }

    private static <T> T ensureType(final Object object) {
        return (T) object;
    }

    public void await()
            throws InterruptedException {
        for (int attempt = 0; handlerInvocationPredicate.shouldAwait(++attempt); ) {
            for (Object waitHandle : waitHandles) {
                indefinite.await(ensureType(waitHandle));
            }
        }
    }

    public boolean await(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        boolean acquired = true;
        long offsetDateTime = dateTimeSupport.now();
        long timeoutRemaining = (long) TIME_UNIT_CONVERTERS.get(new TimeUnitConversionKey(unit, dateTimeSupport.unit())).convert((double) timeout);
        TimeUnit timeUnit = dateTimeSupport.timeUnit();

        if (handlerInvocationPredicate.shouldAwait(1)) {
            for (int i = 0, c = waitHandles.size(); acquired && timeoutRemaining > 0L; i = (i + 1) % c) {
                acquired = timedHandler.await(ensureType(waitHandles.get(i)), timeoutRemaining, timeUnit);

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
    public interface HandlerInvocationPredicate {
        boolean shouldAwait(int attempt);
    }

    @FunctionalInterface
    public interface IndefiniteHandler<T> {
        void await(T waitHandle) throws InterruptedException;
    }

    @FunctionalInterface
    public interface TimedHandler<T> {
        boolean await(T waitHandle, long timeout, TimeUnit unit) throws InterruptedException;
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    private static final class TimeUnitConversionKey {
        private final TimeUnit timeUnit;
        private final Unit<Duration> unit;
    }
}
