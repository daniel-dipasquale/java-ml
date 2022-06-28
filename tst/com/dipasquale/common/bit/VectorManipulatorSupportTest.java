package com.dipasquale.common.bit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class VectorManipulatorSupportTest {
    private static final VectorManipulatorSupport TEST = new VectorManipulatorSupport(2);
    private static final VectorManipulatorSupport MAXIMUM_TEST = VectorManipulatorSupport.create(32);

    @Test
    public void TEST_1() {
        Assertions.assertEquals(32, TEST.getVectorSizePerLong());
    }

    @Test
    public void TEST_2() {
        Assertions.assertFalse(TEST.isWithinBounds(-4L));
        Assertions.assertFalse(TEST.isWithinBounds(-3L));
        Assertions.assertFalse(TEST.isWithinBounds(-2L));
        Assertions.assertFalse(TEST.isWithinBounds(-1L));
        Assertions.assertTrue(TEST.isWithinBounds(0L));
        Assertions.assertTrue(TEST.isWithinBounds(1L));
        Assertions.assertTrue(TEST.isWithinBounds(2L));
        Assertions.assertTrue(TEST.isWithinBounds(3L));
        Assertions.assertFalse(TEST.isWithinBounds(4L));
    }

    @Test
    public void TEST_3() {
        Assertions.assertEquals(0L, TEST.extract(0L, 0));
        Assertions.assertEquals(0L, TEST.extract(0L, 1));
        Assertions.assertEquals(0L, TEST.extract(0L, 2));
        Assertions.assertEquals(0L, TEST.extract(0L, 3));
        Assertions.assertEquals(0L, TEST.extract(0L, 4));
        Assertions.assertEquals(3L, TEST.extract(135L, 0));
        Assertions.assertEquals(1L, TEST.extract(135L, 1));
        Assertions.assertEquals(0L, TEST.extract(135L, 2));
        Assertions.assertEquals(2L, TEST.extract(135L, 3));
        Assertions.assertEquals(0L, TEST.extract(135L, 4));
    }

    @Test
    public void TEST_4() {
        Assertions.assertEquals(0L, TEST.merge(0L, 0, 0L));  // 00
        Assertions.assertEquals(0L, TEST.merge(0L, 1, 0L));  // 00_00
        Assertions.assertEquals(0L, TEST.merge(0L, 2, 0L));  // 00_00_00
        Assertions.assertEquals(1L, TEST.merge(0L, 0, 1L));  // 01
        Assertions.assertEquals(4L, TEST.merge(0L, 1, 1L));  // 01_00
        Assertions.assertEquals(16L, TEST.merge(0L, 2, 1L)); // 01_00_00
        Assertions.assertEquals(2L, TEST.merge(0L, 0, 2L));  // 10
        Assertions.assertEquals(8L, TEST.merge(0L, 1, 2L));  // 10_00
        Assertions.assertEquals(32L, TEST.merge(0L, 2, 2L)); // 10_00_00
        Assertions.assertEquals(3L, TEST.merge(0L, 0, 3L));  // 11
        Assertions.assertEquals(12L, TEST.merge(0L, 1, 3L)); // 11_00
        Assertions.assertEquals(48L, TEST.merge(0L, 2, 3L)); // 11_00_00
        Assertions.assertEquals(0L, TEST.merge(0L, 0, 4L));  // 00
        Assertions.assertEquals(0L, TEST.merge(0L, 1, 4L));  // 00_00
        Assertions.assertEquals(0L, TEST.merge(0L, 2, 4L));  // 00_00_00
    }

    @Test
    public void MAXIMUM_TEST_1() {
        Assertions.assertEquals(2L, MAXIMUM_TEST.getVectorSizePerLong());
    }

    @Test
    public void MAXIMUM_TEST_2() {
        Assertions.assertFalse(MAXIMUM_TEST.isWithinBounds(Long.MIN_VALUE));
        Assertions.assertFalse(MAXIMUM_TEST.isWithinBounds((long) Integer.MIN_VALUE));
        Assertions.assertTrue(MAXIMUM_TEST.isWithinBounds(0L));
        Assertions.assertTrue(MAXIMUM_TEST.isWithinBounds((long) Integer.MAX_VALUE));
        Assertions.assertFalse(MAXIMUM_TEST.isWithinBounds(Long.MAX_VALUE));
    }

    @Test
    public void MAXIMUM_TEST_3() {
        Assertions.assertEquals(0L, MAXIMUM_TEST.extract(0L, 0));
        Assertions.assertEquals(0L, MAXIMUM_TEST.extract(0L, 1));
        Assertions.assertEquals(1L, MAXIMUM_TEST.extract(1L, 0));
        Assertions.assertEquals(0L, MAXIMUM_TEST.extract(1L, 1));
    }

    @Test
    public void MAXIMUM_TEST_4() {
        Assertions.assertEquals(2L, MAXIMUM_TEST.merge(0L, 0, 2L));
        Assertions.assertEquals(8_589_934_592L, MAXIMUM_TEST.merge(0L, 1, 2L));
        Assertions.assertEquals(2L, MAXIMUM_TEST.merge(1L, 0, 2L));
        Assertions.assertEquals(8_589_934_593L, MAXIMUM_TEST.merge(1L, 1, 2L));
    }
}