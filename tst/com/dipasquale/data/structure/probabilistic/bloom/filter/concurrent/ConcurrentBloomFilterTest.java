/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.probabilistic.bloom.filter.concurrent;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.test.JvmWarmup;
import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.common.time.ExpirationFactory;
import com.dipasquale.common.time.ProxyDateTimeSupport;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilter;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.measure.unit.SI;
import java.lang.management.ManagementFactory;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class ConcurrentBloomFilterTest {
    private static final boolean DEBUG_MODE = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
    private static final boolean TEST_AGAINST_BLOOM_FILTER = true; // 8,000,000 items and 3 test cases (and default p) = 613,112,000 bytes without sets
    private static final boolean TEST_AGAINST_SET = true; // 8,000,000 items and 3 test cases = 3,534,360,000 bytes without bloom filters
    private static final int CPU_CORES = DEBUG_MODE ? 1 : Runtime.getRuntime().availableProcessors();
    private static final int SIZE = 500_000;
    private static final int ESTIMATED_SIZE = SIZE;
    private static final int HASH_FUNCTIONS = 8;
    private static final double FALSE_POSITIVE_RATIO = 0.003D;
    private static final int BLOOM_FILTER_PARTITIONS = CPU_CORES * 4;
    private static final long TIMEOUT = 45_000L;
    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.MILLISECONDS;
    private static final AtomicLong CURRENT_DATE_TIME = new AtomicLong();
    private static final DateTimeSupport DATE_TIME_SUPPORT = new ProxyDateTimeSupport(CURRENT_DATE_TIME::get, SI.MILLI(SI.SECOND));
    private static final long EXPIRY_TIME = 1L;
    private static final ExpirationFactory EXPIRY_SUPPORT = DATE_TIME_SUPPORT.createBucketExpirationFactory(EXPIRY_TIME);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(CPU_CORES);
    private static final AtomicInteger INDEX = new AtomicInteger();
    private static final AtomicInteger MISMATCHES = new AtomicInteger();
    private static CountDownLatch COUNT_DOWN_LATCH;

    @BeforeAll
    public static void beforeAll() {
        JvmWarmup.start(250_000);
    }

    @AfterAll
    public static void afterAll() {
        EXECUTOR_SERVICE.shutdownNow();
    }

    private static void queueItemAdditionsConcurrently(final BloomFilter<String> bloomFilter, final Set<String> set) {
        for (int i = 0; i < CPU_CORES; i++) {
            EXECUTOR_SERVICE.execute(() -> {
                while (INDEX.getAndIncrement() < SIZE) {
                    String id = UUID.randomUUID().toString();
                    boolean setAdded = set == null || set.add(id);
                    boolean bloomFilterAdded = bloomFilter == null && setAdded || bloomFilter != null && bloomFilter.add(id) & bloomFilter.mightContain(id);

                    if (bloomFilterAdded != setAdded) {
                        MISMATCHES.incrementAndGet();
                    }
                }

                COUNT_DOWN_LATCH.countDown();
            });
        }
    }

    private static boolean await(final boolean indefinitely)
            throws InterruptedException {
        if (DEBUG_MODE || indefinitely) {
            COUNT_DOWN_LATCH.await();

            return false;
        }

        return !COUNT_DOWN_LATCH.await(TIMEOUT, TIMEOUT_UNIT);
    }

    private static void performTest(final ObjectFactory<BloomFilter<String>> bloomFilterFactory) {
        BloomFilter<String> bloomFilter = !TEST_AGAINST_BLOOM_FILTER ? null : bloomFilterFactory.create();
        Set<String> set = !TEST_AGAINST_SET ? null : Sets.newConcurrentHashSet();

        queueItemAdditionsConcurrently(bloomFilter, set);

        try {
            if (await(false)) {
                INDEX.set(SIZE);
                await(true);
                Assertions.fail("test timed out");
            }
        } catch (AssertionError e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        int setNotAdded = set == null ? 0 : SIZE - set.size();
        int bloomFilterMismatches = MISMATCHES.get();
        String message = String.format("aimed to add: %d, set.notAdded: %d, bloomFilter.mismatches: %d", SIZE, setNotAdded, bloomFilterMismatches);
        double expectedFailures = (double) SIZE * FALSE_POSITIVE_RATIO;
        double actualFailures = (double) bloomFilterMismatches - (double) setNotAdded;

        Assertions.assertEquals(0, setNotAdded);
        Assertions.assertTrue(Double.compare(expectedFailures, actualFailures) >= 0, message);
        System.out.println(message);
    }

    @BeforeEach
    public void beforeEach() {
        CURRENT_DATE_TIME.set(0L);
        COUNT_DOWN_LATCH = new CountDownLatch(CPU_CORES);
        INDEX.set(0);
        MISMATCHES.set(0);
    }

    @Test
    public void TEST_1() {
        performTest(() -> new ConcurrentBloomFilter<>(ESTIMATED_SIZE, HASH_FUNCTIONS));
    }

    @Test
    public void TEST_2() {
        performTest(() -> new ConcurrentBloomFilter<>(ESTIMATED_SIZE, HASH_FUNCTIONS, EXPIRY_SUPPORT));
    }

    @Test
    public void TEST_3() {
        performTest(() -> new ConcurrentBloomFilter<>(ESTIMATED_SIZE, HASH_FUNCTIONS, DATE_TIME_SUPPORT, EXPIRY_TIME, BLOOM_FILTER_PARTITIONS));
    }

    @Test
    public void TEST_4() {
        performTest(() -> new DefaultBloomFilterFactory().createEstimated(ESTIMATED_SIZE, FALSE_POSITIVE_RATIO));
    }
}
