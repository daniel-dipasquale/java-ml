package com.dipasquale.concurrent;

import com.dipasquale.common.RandomSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLongArray;

public final class AtomicLongArrayBitManipulatorSingleBitTest {
    private static final AtomicLongArray ARRAY = new AtomicLongArray(10);
    private static final AtomicLongArrayBitManipulatorSingleBit TEST = new AtomicLongArrayBitManipulatorSingleBit(ARRAY);

    @Before
    public void before() {
        for (int i = 0, c = ARRAY.length(); i < c; i++) {
            ARRAY.set(i, 0L);
        }
    }

    @Test
    public void TEST_1() {
        Assert.assertEquals(640L, TEST.size());
    }

    @Test
    public void TEST_2() {
        Assert.assertTrue(TEST.isOutOfBounds(-1L));
        Assert.assertFalse(TEST.isOutOfBounds(0L));
        Assert.assertFalse(TEST.isOutOfBounds(1L));
        Assert.assertTrue(TEST.isOutOfBounds(2L));
    }

    @Test
    public void TEST_3() {
        for (int i = 0, c = (int) TEST.size(); i < c; i++) {
            Assert.assertEquals(0L, TEST.extract(i));
        }
    }

    @Test
    public void TEST_4() {
        Assert.assertEquals(1L, TEST.merge(0, 1L));
        Assert.assertEquals(1L, TEST.merge(1, 2L));
        Assert.assertEquals(3L, TEST.merge(1, 1L));
    }

    @Test
    public void TEST_5() {
        RandomSupport randomSupport = RandomSupport.create();
        List<Long> randomNumbers = new ArrayList<>();

        for (int i = 0, c = (int) TEST.size(); i < c; i++) {
            long randomNumber = randomSupport.next(0L, 2L);

            TEST.merge(i, randomNumber);
            randomNumbers.add(randomNumber);
        }

        for (int i = 0, c = (int) TEST.size(); i < c; i++) {
            Assert.assertEquals(randomNumbers.get(i).longValue(), TEST.extract(i));
        }
    }

    @Test
    public void TEST_6() {
        Assert.assertEquals(1L, TEST.setAndGet(0, 1L));
        Assert.assertEquals(0L, TEST.setAndGet(0, 0L));
        Assert.assertEquals(1L, TEST.setAndGet(0, 1L));
        Assert.assertEquals(0L, TEST.setAndGet(0, 2L));
        Assert.assertEquals(1L, TEST.setAndGet(3, 1L));
        Assert.assertEquals(0L, TEST.setAndGet(3, 0L));
        Assert.assertEquals(1L, TEST.setAndGet(3, 1L));
        Assert.assertEquals(0L, TEST.setAndGet(3, 2L));
    }

    @Test
    public void TEST_7() {
        Assert.assertEquals(0L, TEST.getAndSet(0, 1L));
        Assert.assertEquals(1L, TEST.getAndSet(0, 0L));
        Assert.assertEquals(0L, TEST.getAndSet(0, 1L));
        Assert.assertEquals(1L, TEST.getAndSet(0, 2L));
        Assert.assertEquals(0L, TEST.getAndSet(3, 1L));
        Assert.assertEquals(1L, TEST.getAndSet(3, 0L));
        Assert.assertEquals(0L, TEST.getAndSet(3, 1L));
        Assert.assertEquals(1L, TEST.getAndSet(3, 2L));
    }

    @Test
    public void TEST_8() {
        Assert.assertEquals(1L, TEST.accumulateAndGet(0, 1L, Long::sum));
        Assert.assertEquals(1L, TEST.accumulateAndGet(0, 0L, Long::sum));
        Assert.assertEquals(0L, TEST.accumulateAndGet(0, 1L, Long::sum));
        Assert.assertEquals(0L, TEST.accumulateAndGet(0, 2L, Long::sum));
        Assert.assertEquals(1L, TEST.accumulateAndGet(3, 1L, Long::sum));
        Assert.assertEquals(1L, TEST.accumulateAndGet(3, 0L, Long::sum));
        Assert.assertEquals(0L, TEST.accumulateAndGet(3, 1L, Long::sum));
        Assert.assertEquals(0L, TEST.accumulateAndGet(3, 2L, Long::sum));
    }

    @Test
    public void TEST_9() {
        Assert.assertEquals(0L, TEST.getAndAccumulate(0, 1L, Long::sum));
        Assert.assertEquals(1L, TEST.getAndAccumulate(0, 0L, Long::sum));
        Assert.assertEquals(1L, TEST.getAndAccumulate(0, 1L, Long::sum));
        Assert.assertEquals(0L, TEST.getAndAccumulate(0, 2L, Long::sum));
        Assert.assertEquals(0L, TEST.getAndAccumulate(3, 1L, Long::sum));
        Assert.assertEquals(1L, TEST.getAndAccumulate(3, 0L, Long::sum));
        Assert.assertEquals(1L, TEST.getAndAccumulate(3, 1L, Long::sum));
        Assert.assertEquals(0L, TEST.getAndAccumulate(3, 2L, Long::sum));
    }
}
