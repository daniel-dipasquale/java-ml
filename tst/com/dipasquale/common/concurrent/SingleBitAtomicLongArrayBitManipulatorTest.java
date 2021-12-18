package com.dipasquale.common.concurrent;

import com.dipasquale.common.bit.concurrent.SingleBitAtomicLongArrayBitManipulator;
import com.dipasquale.common.random.float2.RandomSupport;
import com.dipasquale.common.random.float2.UniformRandomSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLongArray;

public final class SingleBitAtomicLongArrayBitManipulatorTest {
    private static final AtomicLongArray ARRAY = new AtomicLongArray(10);
    private static final SingleBitAtomicLongArrayBitManipulator TEST = new SingleBitAtomicLongArrayBitManipulator(ARRAY);

    @BeforeEach
    public void beforeEach() {
        for (int i = 0, c = ARRAY.length(); i < c; i++) {
            ARRAY.set(i, 0L);
        }
    }

    @Test
    public void TEST_1() {
        Assertions.assertEquals(640L, TEST.size());
    }

    @Test
    public void TEST_2() {
        Assertions.assertTrue(TEST.isOutOfBounds(-1L));
        Assertions.assertFalse(TEST.isOutOfBounds(0L));
        Assertions.assertFalse(TEST.isOutOfBounds(1L));
        Assertions.assertTrue(TEST.isOutOfBounds(2L));
    }

    @Test
    public void TEST_3() {
        for (int i = 0, c = (int) TEST.size(); i < c; i++) {
            Assertions.assertEquals(0L, TEST.extract(i));
        }
    }

    @Test
    public void TEST_4() {
        Assertions.assertEquals(1L, TEST.merge(0, 1L));
        Assertions.assertEquals(1L, TEST.merge(1, 2L));
        Assertions.assertEquals(3L, TEST.merge(1, 1L));
    }

    @Test
    public void TEST_5() {
        RandomSupport randomSupport = new UniformRandomSupport();
        List<Long> randomNumbers = new ArrayList<>();

        for (int i = 0, c = (int) TEST.size(); i < c; i++) {
            long randomNumber = randomSupport.next(0L, 2L);

            TEST.merge(i, randomNumber);
            randomNumbers.add(randomNumber);
        }

        for (int i = 0, c = (int) TEST.size(); i < c; i++) {
            Assertions.assertEquals(randomNumbers.get(i).longValue(), TEST.extract(i));
        }
    }

    @Test
    public void TEST_6() {
        Assertions.assertEquals(1L, TEST.setAndGet(0, 1L));
        Assertions.assertEquals(0L, TEST.setAndGet(0, 0L));
        Assertions.assertEquals(1L, TEST.setAndGet(0, 1L));
        Assertions.assertEquals(0L, TEST.setAndGet(0, 2L));
        Assertions.assertEquals(1L, TEST.setAndGet(3, 1L));
        Assertions.assertEquals(0L, TEST.setAndGet(3, 0L));
        Assertions.assertEquals(1L, TEST.setAndGet(3, 1L));
        Assertions.assertEquals(0L, TEST.setAndGet(3, 2L));
    }

    @Test
    public void TEST_7() {
        Assertions.assertEquals(0L, TEST.getAndSet(0, 1L));
        Assertions.assertEquals(1L, TEST.getAndSet(0, 0L));
        Assertions.assertEquals(0L, TEST.getAndSet(0, 1L));
        Assertions.assertEquals(1L, TEST.getAndSet(0, 2L));
        Assertions.assertEquals(0L, TEST.getAndSet(3, 1L));
        Assertions.assertEquals(1L, TEST.getAndSet(3, 0L));
        Assertions.assertEquals(0L, TEST.getAndSet(3, 1L));
        Assertions.assertEquals(1L, TEST.getAndSet(3, 2L));
    }

    @Test
    public void TEST_8() {
        Assertions.assertEquals(1L, TEST.accumulateAndGet(0, 1L, Long::sum));
        Assertions.assertEquals(1L, TEST.accumulateAndGet(0, 0L, Long::sum));
        Assertions.assertEquals(0L, TEST.accumulateAndGet(0, 1L, Long::sum));
        Assertions.assertEquals(0L, TEST.accumulateAndGet(0, 2L, Long::sum));
        Assertions.assertEquals(1L, TEST.accumulateAndGet(3, 1L, Long::sum));
        Assertions.assertEquals(1L, TEST.accumulateAndGet(3, 0L, Long::sum));
        Assertions.assertEquals(0L, TEST.accumulateAndGet(3, 1L, Long::sum));
        Assertions.assertEquals(0L, TEST.accumulateAndGet(3, 2L, Long::sum));
    }

    @Test
    public void TEST_9() {
        Assertions.assertEquals(0L, TEST.getAndAccumulate(0, 1L, Long::sum));
        Assertions.assertEquals(1L, TEST.getAndAccumulate(0, 0L, Long::sum));
        Assertions.assertEquals(1L, TEST.getAndAccumulate(0, 1L, Long::sum));
        Assertions.assertEquals(0L, TEST.getAndAccumulate(0, 2L, Long::sum));
        Assertions.assertEquals(0L, TEST.getAndAccumulate(3, 1L, Long::sum));
        Assertions.assertEquals(1L, TEST.getAndAccumulate(3, 0L, Long::sum));
        Assertions.assertEquals(1L, TEST.getAndAccumulate(3, 1L, Long::sum));
        Assertions.assertEquals(0L, TEST.getAndAccumulate(3, 2L, Long::sum));
    }
}
