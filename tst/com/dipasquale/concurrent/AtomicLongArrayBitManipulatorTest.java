package com.dipasquale.concurrent;

import com.dipasquale.common.RandomSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLongArray;

public final class AtomicLongArrayBitManipulatorTest {
    private static final AtomicLongArray ARRAY = new AtomicLongArray(2);
    private static final AtomicLongArrayBitManipulator TEST = new AtomicLongArrayBitManipulator(ARRAY, 10);

    @BeforeEach
    public void beforeEach() {
        for (int i = 0, c = ARRAY.length(); i < c; i++) {
            ARRAY.set(i, 0L);
        }
    }

    @Test
    public void TEST_1() {
        Assertions.assertEquals(12L, TEST.size());
    }

    @Test
    public void TEST_2() {
        Assertions.assertTrue(TEST.isOutOfBounds(-1L));
        Assertions.assertFalse(TEST.isOutOfBounds(0L));
        Assertions.assertFalse(TEST.isOutOfBounds(1_023L));
        Assertions.assertTrue(TEST.isOutOfBounds(1_024L));
    }

    @Test
    public void TEST_3() {
        for (int i = 0, c = (int) TEST.size(); i < c; i++) {
            Assertions.assertEquals(0L, TEST.extract(i));
        }
    }

    @Test
    public void TEST_4() {
        Assertions.assertEquals(572L, TEST.merge(0, 572L));
        Assertions.assertEquals(572L, TEST.merge(1, 1_024L));
        Assertions.assertEquals(1_048_124L, TEST.merge(1, 1_023L));
    }

    @Test
    public void TEST_5() {
        RandomSupport randomSupport = RandomSupport.create(false);
        List<Long> randomNumbers = new ArrayList<>();

        for (int i = 0, c = (int) TEST.size(); i < c; i++) {
            long randomNumber = randomSupport.next(0L, 1_024L);

            TEST.merge(i, randomNumber);
            randomNumbers.add(randomNumber);
        }

        for (int i = 0, c = (int) TEST.size(); i < c; i++) {
            Assertions.assertEquals(randomNumbers.get(i).longValue(), TEST.extract(i));
        }
    }

    @Test
    public void TEST_6() {
        Assertions.assertEquals(5L, TEST.setAndGet(0, 5L));
        Assertions.assertEquals(2L, TEST.setAndGet(0, 2L));
        Assertions.assertEquals(9L, TEST.setAndGet(0, 9L));
        Assertions.assertEquals(1_023L, TEST.setAndGet(0, 1_023L));
        Assertions.assertEquals(0L, TEST.setAndGet(0, 1_024L));
        Assertions.assertEquals(4L, TEST.setAndGet(3, 4L));
        Assertions.assertEquals(2L, TEST.setAndGet(3, 2L));
        Assertions.assertEquals(3L, TEST.setAndGet(3, 3L));
        Assertions.assertEquals(1_023L, TEST.setAndGet(3, 1_023L));
        Assertions.assertEquals(0L, TEST.setAndGet(3, 1_024L));
    }

    @Test
    public void TEST_7() {
        Assertions.assertEquals(0L, TEST.getAndSet(0, 5L));
        Assertions.assertEquals(5L, TEST.getAndSet(0, 2L));
        Assertions.assertEquals(2L, TEST.getAndSet(0, 9L));
        Assertions.assertEquals(9L, TEST.getAndSet(0, 1_023L));
        Assertions.assertEquals(1_023L, TEST.getAndSet(0, 1_024L));
        Assertions.assertEquals(0L, TEST.getAndSet(3, 4L));
        Assertions.assertEquals(4L, TEST.getAndSet(3, 2L));
        Assertions.assertEquals(2L, TEST.getAndSet(3, 3L));
        Assertions.assertEquals(3L, TEST.getAndSet(3, 1_023L));
        Assertions.assertEquals(1_023L, TEST.getAndSet(3, 1_024L));
    }

    @Test
    public void TEST_8() {
        Assertions.assertEquals(5L, TEST.accumulateAndGet(0, 5L, Long::sum));
        Assertions.assertEquals(3L, TEST.accumulateAndGet(0, -2L, Long::sum));
        Assertions.assertEquals(12L, TEST.accumulateAndGet(0, 9L, Long::sum));
        Assertions.assertEquals(1_023L, TEST.accumulateAndGet(0, 1_011L, Long::sum));
        Assertions.assertEquals(1L, TEST.accumulateAndGet(0, 2L, Long::sum));
        Assertions.assertEquals(4L, TEST.accumulateAndGet(3, 4L, Long::sum));
        Assertions.assertEquals(2L, TEST.accumulateAndGet(3, -2L, Long::sum));
        Assertions.assertEquals(0L, TEST.accumulateAndGet(3, -2L, Long::sum));
        Assertions.assertEquals(0L, TEST.accumulateAndGet(3, 1_024L, Long::sum));
        Assertions.assertEquals(1L, TEST.accumulateAndGet(3, 1L, Long::sum));
    }

    @Test
    public void TEST_9() {
        Assertions.assertEquals(0L, TEST.getAndAccumulate(0, 5L, Long::sum));
        Assertions.assertEquals(5L, TEST.getAndAccumulate(0, -2L, Long::sum));
        Assertions.assertEquals(3L, TEST.getAndAccumulate(0, 9L, Long::sum));
        Assertions.assertEquals(12L, TEST.getAndAccumulate(0, 1_011L, Long::sum));
        Assertions.assertEquals(1_023L, TEST.getAndAccumulate(0, 2L, Long::sum));
        Assertions.assertEquals(0L, TEST.getAndAccumulate(3, 4L, Long::sum));
        Assertions.assertEquals(4L, TEST.getAndAccumulate(3, -2L, Long::sum));
        Assertions.assertEquals(2L, TEST.getAndAccumulate(3, -2L, Long::sum));
        Assertions.assertEquals(0L, TEST.getAndAccumulate(3, 1_024L, Long::sum));
        Assertions.assertEquals(0L, TEST.getAndAccumulate(3, 1L, Long::sum));
    }
}
