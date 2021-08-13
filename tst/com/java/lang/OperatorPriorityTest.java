/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.java.lang;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public final class OperatorPriorityTest {
    @Test
    @Disabled
    public void TEST_1() {
        long x = 10;
        long y = 187;
        long z = 180;

        x -= y - z;

        Assertions.assertEquals(3, x);
    }
}
