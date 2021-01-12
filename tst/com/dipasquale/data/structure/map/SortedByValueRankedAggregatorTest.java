package com.dipasquale.data.structure.map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class SortedByValueRankedAggregatorTest {
    private static final SortedByValueRankedAggregator<String, Long> TEST = SortedByValueRankedAggregator.createHighestRankedConcurrent(2);

    @Before
    public void before() {
        TEST.clear();
    }

    @Test
    public void TEST_1() {
        Assert.assertEquals(Long.valueOf(0L), TEST.getExtremeValue());
        Assert.assertEquals(ImmutableSet.of(), TEST.getKeys());
    }

    @Test
    public void TEST_2() {
        Assert.assertNull(TEST.put("one", 1L));
        Assert.assertEquals(Long.valueOf(0L), TEST.getExtremeValue());
        Assert.assertEquals(ImmutableSet.of("one"), TEST.getKeys());
        Assert.assertNull(TEST.put("two", 2L));
        Assert.assertEquals(Long.valueOf(0L), TEST.getExtremeValue());
        Assert.assertEquals(ImmutableSet.of("one", "two"), TEST.getKeys());
        Assert.assertNull(TEST.put("three", 3L));
        Assert.assertEquals(Long.valueOf(2L), TEST.getExtremeValue());
        Assert.assertEquals(ImmutableSet.of("two", "three"), TEST.getKeys());
        Assert.assertNull(TEST.put("zero", 0L));
        Assert.assertEquals(Long.valueOf(2L), TEST.getExtremeValue());
        Assert.assertEquals(ImmutableSet.of("two", "three"), TEST.getKeys());
        Assert.assertNull(TEST.put("four", 4L));
        Assert.assertEquals(Long.valueOf(3L), TEST.getExtremeValue());
        Assert.assertEquals(ImmutableSet.of("three", "four"), TEST.getKeys());
    }

    @Test
    public void TEST_3() {
        Assert.assertNull(TEST.put("one", 1L));
        Assert.assertNull(TEST.put("two", 2L));
        Assert.assertNull(TEST.put("three", 3L));
        Assert.assertNull(TEST.put("zero", 0L));
        Assert.assertNull(TEST.put("four", 4L));

        Assert.assertEquals(ImmutableList.<String>builder()
                .add("four")
                .add("three")
                .build(), TEST.clear().retrieve());
    }
}
