package com.dipasquale.threading.wait.handle;

import com.dipasquale.common.time.DateTimeSupport;
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
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class MultiWaitHandle implements WaitHandle {
    private static final Map<TimeUnitConversionKey, UnitConverter> TIME_UNIT_CONVERTERS = createTimeUnitConverters();
    private final List<? extends WaitHandle> waitHandles;
    private final DateTimeSupport dateTimeSupport;
    private final WaitHandleStrategy waitHandleStrategy;

    public MultiWaitHandle(final List<? extends WaitHandle> waitHandles, final DateTimeSupport dateTimeSupport) {
        this(waitHandles, dateTimeSupport, a -> a == 1);
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

    private static <TWaitHandle extends WaitHandle, TItem> List<TWaitHandle> adapt(final List<TItem> items, final WaitHandleFactory<TItem, TWaitHandle> waitHandlerFactory) {
        return items.stream()
                .map(waitHandlerFactory::create)
                .collect(Collectors.toList());
    }

    public static <TWaitHandle extends WaitHandle, TItem> MultiWaitHandle create(final List<TItem> items, final WaitHandleFactory<TItem, TWaitHandle> waitHandlerFactory, final DateTimeSupport dateTimeSupport, final WaitHandleStrategy waitHandleStrategy) {
        List<TWaitHandle> waitHandles = adapt(items, waitHandlerFactory);

        return new MultiWaitHandle(waitHandles, dateTimeSupport, waitHandleStrategy);
    }

    public static <TWaitHandle extends WaitHandle, TItem> MultiWaitHandle create(final List<TItem> items, final WaitHandleFactory<TItem, TWaitHandle> waitHandlerFactory, final DateTimeSupport dateTimeSupport) {
        List<TWaitHandle> waitHandles = adapt(items, waitHandlerFactory);

        return new MultiWaitHandle(waitHandles, dateTimeSupport);
    }

    @Override
    public void await()
            throws InterruptedException {
        for (int attempt = 0; waitHandleStrategy.shouldAwait(++attempt); ) {
            for (WaitHandle waitHandle : waitHandles) {
                waitHandle.await();
            }
        }
    }

    @Override
    public boolean await(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        boolean acquired = true;
        long offsetDateTime = dateTimeSupport.now();
        TimeUnitConversionKey conversionKey = new TimeUnitConversionKey(unit, dateTimeSupport.unit());
        long timeoutRemaining = (long) TIME_UNIT_CONVERTERS.get(conversionKey).convert((double) timeout);
        TimeUnit timeUnit = dateTimeSupport.timeUnit();

        for (int attempt = 0; acquired && timeoutRemaining > 0L && waitHandleStrategy.shouldAwait(++attempt); ) {
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

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    private static final class TimeUnitConversionKey {
        private final TimeUnit timeUnit;
        private final Unit<Duration> unit;
    }
}
