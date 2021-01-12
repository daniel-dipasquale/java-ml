package com.dipasquale.data.structure.probabilistic;

import org.junit.Assert;
import org.junit.Test;

public final class DataStructureMultiCalculatorTest {
    private static final DataStructureMultiCalculator TEST = DataStructureMultiCalculator.getInstance();

    @Test
    public void TEST_1() {
        DataStructureMultiCalculator.Result result = TEST.readjust(1, Integer.MAX_VALUE, Integer.MAX_VALUE);

        Assert.assertEquals(DataStructureMultiCalculator.Result.builder()
                .count(1)
                .estimatedSize(Integer.MAX_VALUE)
                .size(Integer.MAX_VALUE)
                .build(), result);
    }

    @Test
    public void TEST_2() {
        DataStructureMultiCalculator.Result result = TEST.readjust(2, Integer.MAX_VALUE, Integer.MAX_VALUE);

        Assert.assertEquals(DataStructureMultiCalculator.Result.builder()
                .count(2)
                .estimatedSize(Integer.MAX_VALUE)
                .size(Integer.MAX_VALUE)
                .build(), result);
    }

    @Test
    public void TEST_3() {
        DataStructureMultiCalculator.Result result = TEST.readjust(3, Integer.MAX_VALUE, Integer.MAX_VALUE * 2L);

        Assert.assertEquals(DataStructureMultiCalculator.Result.builder()
                .count(6)
                .estimatedSize((int) Math.ceil((double) Integer.MAX_VALUE / 2D))
                .size(Integer.MAX_VALUE)
                .build(), result);
    }
}
