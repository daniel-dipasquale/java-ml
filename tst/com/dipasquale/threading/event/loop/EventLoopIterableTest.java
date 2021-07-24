package com.dipasquale.threading.event.loop;

import com.dipasquale.common.error.ErrorLogger;
import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.common.time.ProxyDateTimeSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.measure.unit.SI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class EventLoopIterableTest {
    private static final int NUMBER_OF_THREADS = 4;
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static final List<Throwable> EXCEPTIONS = Collections.synchronizedList(new ArrayList<>());
    private static final ErrorLogger EXCEPTION_LOGGER = EXCEPTIONS::add;
    private static final AtomicLong CURRENT_DATE_TIME = new AtomicLong();
    private static final DateTimeSupport DATE_TIME_SUPPORT = new ProxyDateTimeSupport(CURRENT_DATE_TIME::get, SI.MILLI(SI.SECOND));

    private static final EventLoopIterableSettings SETTINGS = EventLoopIterableSettings.builder()
            .executorService(EXECUTOR_SERVICE)
            .numberOfThreads(NUMBER_OF_THREADS)
            .errorLogger(EXCEPTION_LOGGER)
            .dateTimeSupport(DATE_TIME_SUPPORT)
            .build();

    @AfterAll
    public static void afterAll() {
        EXECUTOR_SERVICE.shutdown();
    }

    @BeforeEach
    public void beforeEach() {
        EXCEPTIONS.clear();
    }

    @Test
    public void TEST_1() {
        EventLoopIterable test = new EventLoopIterable(SETTINGS);
        ItemCollector collector = new ItemCollector();

        List<Item> items = IntStream.range(0, 256)
                .mapToObj(i -> new Item((long) i + 1L))
                .collect(Collectors.toList());

        try {
            test.queue(items.iterator(), i -> collector.value.addAndGet(i.value));
            test.awaitUntilDone();
            Assertions.assertEquals(32_896L, collector.value.get());
        } catch (InterruptedException e) {
            Assertions.fail("interrupted");
        } finally {
            test.shutdown();
        }
    }

    @Test
    public void TEST_2() {
        EventLoopIterable test = new EventLoopIterable(SETTINGS);
        ItemCollector collector = new ItemCollector();

        List<Item> items = IntStream.range(0, 256)
                .mapToObj(i -> new Item((long) i + 1L))
                .collect(Collectors.toList());

        try {
            test.queue(items.stream().peek(i -> i.value++).iterator(), i -> collector.value.addAndGet(i.value));
            test.awaitUntilDone();
            Assertions.assertEquals(33_152L, collector.value.get());
        } catch (InterruptedException e) {
            Assertions.fail("interrupted");
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
