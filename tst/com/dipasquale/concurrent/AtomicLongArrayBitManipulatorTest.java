package com.dipasquale.concurrent;

import com.dipasquale.common.RandomSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLongArray;

public final class AtomicLongArrayBitManipulatorTest {
    private static final AtomicLongArray ARRAY = new AtomicLongArray(2);
    private static final AtomicLongArrayBitManipulator TEST = new AtomicLongArrayBitManipulator(ARRAY, 10);

    @Before
    public void before() {
        for (int i = 0, c = ARRAY.length(); i < c; i++) {
            ARRAY.set(i, 0L);
        }
    }

    @Test
    public void TEST_1() {
        Assert.assertEquals(12L, TEST.size());
    }

    @Test
    public void TEST_2() {
        Assert.assertTrue(TEST.isOutOfBounds(-1L));
        Assert.assertFalse(TEST.isOutOfBounds(0L));
        Assert.assertFalse(TEST.isOutOfBounds(1_023L));
        Assert.assertTrue(TEST.isOutOfBounds(1_024L));
    }

    @Test
    public void TEST_3() {
        for (int i = 0, c = (int) TEST.size(); i < c; i++) {
            Assert.assertEquals(0L, TEST.extract(i));
        }
    }

    @Test
    public void TEST_4() {
        Assert.assertEquals(572L, TEST.merge(0, 572L));
        Assert.assertEquals(572L, TEST.merge(1, 1_024L));
        Assert.assertEquals(1_048_124L, TEST.merge(1, 1_023L));
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
            Assert.assertEquals(randomNumbers.get(i).longValue(), TEST.extract(i));
        }
    }

    @Test
    public void TEST_6() {
        Assert.assertEquals(5L, TEST.setAndGet(0, 5L));
        Assert.assertEquals(2L, TEST.setAndGet(0, 2L));
        Assert.assertEquals(9L, TEST.setAndGet(0, 9L));
        Assert.assertEquals(1_023L, TEST.setAndGet(0, 1_023L));
        Assert.assertEquals(0L, TEST.setAndGet(0, 1_024L));
        Assert.assertEquals(4L, TEST.setAndGet(3, 4L));
        Assert.assertEquals(2L, TEST.setAndGet(3, 2L));
        Assert.assertEquals(3L, TEST.setAndGet(3, 3L));
        Assert.assertEquals(1_023L, TEST.setAndGet(3, 1_023L));
        Assert.assertEquals(0L, TEST.setAndGet(3, 1_024L));
    }

    @Test
    public void TEST_7() {
        Assert.assertEquals(0L, TEST.getAndSet(0, 5L));
        Assert.assertEquals(5L, TEST.getAndSet(0, 2L));
        Assert.assertEquals(2L, TEST.getAndSet(0, 9L));
        Assert.assertEquals(9L, TEST.getAndSet(0, 1_023L));
        Assert.assertEquals(1_023L, TEST.getAndSet(0, 1_024L));
        Assert.assertEquals(0L, TEST.getAndSet(3, 4L));
        Assert.assertEquals(4L, TEST.getAndSet(3, 2L));
        Assert.assertEquals(2L, TEST.getAndSet(3, 3L));
        Assert.assertEquals(3L, TEST.getAndSet(3, 1_023L));
        Assert.assertEquals(1_023L, TEST.getAndSet(3, 1_024L));
    }

    @Test
    public void TEST_8() {
        Assert.assertEquals(5L, TEST.accumulateAndGet(0, 5L, Long::sum));
        Assert.assertEquals(3L, TEST.accumulateAndGet(0, -2L, Long::sum));
        Assert.assertEquals(12L, TEST.accumulateAndGet(0, 9L, Long::sum));
        Assert.assertEquals(1_023L, TEST.accumulateAndGet(0, 1_011L, Long::sum));
        Assert.assertEquals(1L, TEST.accumulateAndGet(0, 2L, Long::sum));
        Assert.assertEquals(4L, TEST.accumulateAndGet(3, 4L, Long::sum));
        Assert.assertEquals(2L, TEST.accumulateAndGet(3, -2L, Long::sum));
        Assert.assertEquals(0L, TEST.accumulateAndGet(3, -2L, Long::sum));
        Assert.assertEquals(0L, TEST.accumulateAndGet(3, 1_024L, Long::sum));
        Assert.assertEquals(1L, TEST.accumulateAndGet(3, 1L, Long::sum));
    }

    @Test
    public void TEST_9() {
        Assert.assertEquals(0L, TEST.getAndAccumulate(0, 5L, Long::sum));
        Assert.assertEquals(5L, TEST.getAndAccumulate(0, -2L, Long::sum));
        Assert.assertEquals(3L, TEST.getAndAccumulate(0, 9L, Long::sum));
        Assert.assertEquals(12L, TEST.getAndAccumulate(0, 1_011L, Long::sum));
        Assert.assertEquals(1_023L, TEST.getAndAccumulate(0, 2L, Long::sum));
        Assert.assertEquals(0L, TEST.getAndAccumulate(3, 4L, Long::sum));
        Assert.assertEquals(4L, TEST.getAndAccumulate(3, -2L, Long::sum));
        Assert.assertEquals(2L, TEST.getAndAccumulate(3, -2L, Long::sum));
        Assert.assertEquals(0L, TEST.getAndAccumulate(3, 1_024L, Long::sum));
        Assert.assertEquals(0L, TEST.getAndAccumulate(3, 1L, Long::sum));
    }
}
