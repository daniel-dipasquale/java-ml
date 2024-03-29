package com.dipasquale.synchronization.event.loop;

import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.common.time.ProxyDateTimeSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

public final class ParallelEventLoopTest {
    private static final int CONCURRENCY_LEVEL = 4;
    private static final List<Throwable> EXCEPTIONS = Collections.synchronizedList(new ArrayList<>());
    private static final ErrorHandler EXCEPTION_LOGGER = EXCEPTIONS::add;
    private static final AtomicLong CURRENT_DATE_TIME = new AtomicLong();
    private static final DateTimeSupport DATE_TIME_SUPPORT = new ProxyDateTimeSupport(CURRENT_DATE_TIME::get, TimeUnit.MILLISECONDS);

    private static final ParallelEventLoopSettings SETTINGS = ParallelEventLoopSettings.builder()
            .concurrencyLevel(CONCURRENCY_LEVEL)
            .errorHandler(EXCEPTION_LOGGER)
            .dateTimeSupport(DATE_TIME_SUPPORT)
            .build();

    @BeforeEach
    public void beforeEach() {
        EXCEPTIONS.clear();
    }

    @Test
    public void TEST_1() {
        ParallelEventLoop test = new ParallelEventLoop(SETTINGS);
        ElementCollector collector = new ElementCollector();

        List<Element> elements = IntStream.range(0, 256)
                .mapToObj(index -> new Element((long) index + 1L))
                .toList();

        try {
            test.queue(elements.iterator(), ElementHandler.adapt(element -> collector.value.addAndGet(element.value)));
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
        ParallelEventLoop test = new ParallelEventLoop(SETTINGS);
        ElementCollector collector = new ElementCollector();

        List<Element> elements = IntStream.range(0, 256)
                .mapToObj(index -> new Element((long) index + 1L))
                .toList();

        Iterator<Element> elementIterator = elements.stream()
                .peek(element -> element.value++)
                .iterator();

        try {

            test.queue(elementIterator, ElementHandler.adapt(element -> collector.value.addAndGet(element.value)));
            test.awaitUntilDone();
            Assertions.assertEquals(33_152L, collector.value.get());
        } catch (InterruptedException e) {
            Assertions.fail("interrupted");
        } finally {
            test.shutdown();
        }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Element {
        private long value;
    }

    private static final class ElementCollector {
        private final AtomicLong value = new AtomicLong();
    }
}
