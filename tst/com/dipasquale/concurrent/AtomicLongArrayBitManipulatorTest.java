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
        Assert.assertEquals(ARRAY.length() * 6, TEST.size());
    }

    @Test
    public void TEST_2() {
        for (int i = 0, c = (int) TEST.size(); i < c; i++) {
            Assert.assertEquals(0L, TEST.extract(i));
        }
    }

    @Test
    public void TEST_3() {
        Assert.assertEquals(572L, TEST.merge(0, 572L));
    }

    @Test
    public void TEST_4() {
        RandomSupport randomSupport = RandomSupport.create();
        List<Long> randomNumbers = new ArrayList<>();

        for (int i = 0, c = (int) TEST.size(); i < c; i++) {
            long randomNumber = randomSupport.next(0L, 1_023L);

            TEST.merge(i, randomNumber);
            randomNumbers.add(randomNumber);
        }

        for (int i = 0, c = (int) TEST.size(); i < c; i++) {
            Assert.assertEquals(randomNumbers.get(i).longValue(), TEST.extract(i));
        }
    }

    @Test
    public void TEST_5() {
        Assert.assertEquals(5L, TEST.setAndGet(0, 5L));
        Assert.assertEquals(2L, TEST.setAndGet(0, 2L));
        Assert.assertEquals(9L, TEST.setAndGet(0, 9L));
        Assert.assertEquals(4L, TEST.setAndGet(3, 4L));
        Assert.assertEquals(2L, TEST.setAndGet(3, 2L));
        Assert.assertEquals(3L, TEST.setAndGet(3, 3L));
    }

    @Test
    public void TEST_6() {
        Assert.assertEquals(0L, TEST.getAndSet(0, 5L));
        Assert.assertEquals(5L, TEST.getAndSet(0, 2L));
        Assert.assertEquals(2L, TEST.getAndSet(0, 9L));
        Assert.assertEquals(0L, TEST.getAndSet(3, 4L));
        Assert.assertEquals(4L, TEST.getAndSet(3, 2L));
        Assert.assertEquals(2L, TEST.getAndSet(3, 3L));
    }

    @Test
    public void TEST_7() {
        Assert.assertEquals(5L, TEST.accumulateAndGet(0, 5L, Long::sum));
        Assert.assertEquals(3L, TEST.accumulateAndGet(0, -2L, Long::sum));
        Assert.assertEquals(12L, TEST.accumulateAndGet(0, 9L, Long::sum));
        Assert.assertEquals(4L, TEST.accumulateAndGet(3, 4L, Long::sum));
        Assert.assertEquals(2L, TEST.accumulateAndGet(3, -2L, Long::sum));
        Assert.assertEquals(0L, TEST.accumulateAndGet(3, -2L, Long::sum));
    }

    @Test
    public void TEST_8() {
        Assert.assertEquals(0L, TEST.getAndAccumulate(0, 5L, Long::sum));
        Assert.assertEquals(5L, TEST.getAndAccumulate(0, -2L, Long::sum));
        Assert.assertEquals(3L, TEST.getAndAccumulate(0, 9L, Long::sum));
        Assert.assertEquals(0L, TEST.getAndAccumulate(3, 4L, Long::sum));
        Assert.assertEquals(4L, TEST.getAndAccumulate(3, -2L, Long::sum));
        Assert.assertEquals(2L, TEST.getAndAccumulate(3, -2L, Long::sum));
    }
}
