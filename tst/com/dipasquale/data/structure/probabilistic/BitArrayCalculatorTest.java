/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.probabilistic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class BitArrayCalculatorTest {
    @Test
    public void TEST_1() {
        BitArrayCalculator.Result result = BitArrayCalculator.readjust(1, Integer.MAX_VALUE, Integer.MAX_VALUE);

        Assertions.assertEquals(BitArrayCalculator.Result.builder()
                .count(1)
                .estimatedSize(Integer.MAX_VALUE)
                .size(Integer.MAX_VALUE)
                .build(), result);
    }

    @Test
    public void TEST_2() {
        BitArrayCalculator.Result result = BitArrayCalculator.readjust(2, Integer.MAX_VALUE, Integer.MAX_VALUE);

        Assertions.assertEquals(BitArrayCalculator.Result.builder()
                .count(2)
                .estimatedSize(Integer.MAX_VALUE)
                .size(Integer.MAX_VALUE)
                .build(), result);
    }

    @Test
    public void TEST_3() {
        BitArrayCalculator.Result result = BitArrayCalculator.readjust(3, Integer.MAX_VALUE, Integer.MAX_VALUE * 2L);

        Assertions.assertEquals(BitArrayCalculator.Result.builder()
                .count(6)
                .estimatedSize((int) Math.ceil((double) Integer.MAX_VALUE / 2D))
                .size(Integer.MAX_VALUE)
                .build(), result);
    }
}
