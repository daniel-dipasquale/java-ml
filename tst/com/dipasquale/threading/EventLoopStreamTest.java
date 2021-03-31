package com.dipasquale.threading;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.ExceptionLogger;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.measure.unit.SI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class EventLoopStreamTest {
    private static final int NUMBER_OF_THREADS = 4;
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static final List<Throwable> EXCEPTIONS = Collections.synchronizedList(new ArrayList<>());
    private static final ExceptionLogger EXCEPTION_LOGGER = EXCEPTIONS::add;
    private static final AtomicLong CURRENT_DATE_TIME = new AtomicLong();
    private static final DateTimeSupport DATE_TIME_SUPPORT = DateTimeSupport.create(CURRENT_DATE_TIME::get, SI.MILLI(SI.SECOND));

    private static final EventLoopStreamSettings SETTINGS = EventLoopStreamSettings.builder()
            .executorService(EXECUTOR_SERVICE)
            .numberOfThreads(NUMBER_OF_THREADS)
            .exceptionLogger(EXCEPTION_LOGGER)
            .dateTimeSupport(DATE_TIME_SUPPORT)
            .build();

    @AfterClass
    public static void afterClass() {
        EXECUTOR_SERVICE.shutdown();
    }

    @Before
    public void before() {
        EXCEPTIONS.clear();
    }

    @Test
    public void TEST_1() {
        EventLoopStream test = new EventLoopStream(SETTINGS);
        ItemCollector collector = new ItemCollector();

        List<Item> items = IntStream.range(0, 256)
                .mapToObj(i -> new Item((long) i + 1L))
                .collect(Collectors.toList());

        try {
            test.queue(items.stream(), i -> collector.value.addAndGet(i.value));
            test.awaitUntilDone();
            Assert.assertEquals(32_896L, collector.value.get());
        } catch (InterruptedException e) {
            Assert.fail("interrupted");
        } finally {
            test.shutdown();
        }
    }

    @Test
    public void TEST_2() {
        EventLoopStream test = new EventLoopStream(SETTINGS);
        ItemCollector collector = new ItemCollector();

        List<Item> items = IntStream.range(0, 256)
                .mapToObj(i -> new Item((long) i + 1L))
                .collect(Collectors.toList());

        try {
            test.queue(items.stream().peek(i -> i.value++), i -> collector.value.addAndGet(i.value));
            test.awaitUntilDone();
            Assert.assertEquals(33_152L, collector.value.get());
        } catch (InterruptedException e) {
            Assert.fail("interrupted");
        } finally {
            test.shutdown();
        }
    }

    @AllArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class Item {
        private long value;
    }

    private static final class ItemCollector {
        private final AtomicLong value = new AtomicLong(0L);
    }
}
