package com.dipasquale.data.structure.map.concurrent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

public final class ConcurrentBoundedHeapTest {
    private static final ConcurrentBoundedHeap<String, Long> TEST = ConcurrentBoundedHeap.createDescendingOrder(0L, 2);

    @BeforeEach
    public void beforeEach() {
        TEST.clear();
    }

    @Test
    public void TEST_1() {
        Assertions.assertEquals(Long.valueOf(0L), TEST.getFirstValue());
        Assertions.assertEquals(Set.of(), TEST.getKeys());
    }

    @Test
    public void TEST_2() {
        Assertions.assertNull(TEST.put("one", 1L));
        Assertions.assertEquals(Long.valueOf(0L), TEST.getFirstValue());
        Assertions.assertEquals(Set.of("one"), TEST.getKeys());
        Assertions.assertNull(TEST.put("two", 2L));
        Assertions.assertEquals(Long.valueOf(0L), TEST.getFirstValue());
        Assertions.assertEquals(Set.of("one", "two"), TEST.getKeys());
        Assertions.assertNull(TEST.put("three", 3L));
        Assertions.assertEquals(Long.valueOf(2L), TEST.getFirstValue());
        Assertions.assertEquals(Set.of("two", "three"), TEST.getKeys());
        Assertions.assertNull(TEST.put("zero", 0L));
        Assertions.assertEquals(Long.valueOf(2L), TEST.getFirstValue());
        Assertions.assertEquals(Set.of("two", "three"), TEST.getKeys());
        Assertions.assertNull(TEST.put("four", 4L));
        Assertions.assertEquals(Long.valueOf(3L), TEST.getFirstValue());
        Assertions.assertEquals(Set.of("three", "four"), TEST.getKeys());
    }

    @Test
    public void TEST_3() {
        Assertions.assertNull(TEST.put("one", 1L));
        Assertions.assertNull(TEST.put("two", 2L));
        Assertions.assertNull(TEST.put("three", 3L));
        Assertions.assertNull(TEST.put("zero", 0L));
        Assertions.assertNull(TEST.put("four", 4L));
        Assertions.assertEquals(List.of("four", "three"), TEST.clear().getKeys());
    }
}
