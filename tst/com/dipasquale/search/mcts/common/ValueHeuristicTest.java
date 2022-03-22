package com.dipasquale.search.mcts.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class ValueHeuristicTest {
    @Test
    public void TEST_1() {
        Assertions.assertEquals(-0.8741074f, ValueHeuristic.calculateUnbounded(-1_000_000f));
        Assertions.assertEquals(-0.8221722f, ValueHeuristic.calculateUnbounded(-100_000f));
        Assertions.assertEquals(-0.74881506f, ValueHeuristic.calculateUnbounded(-10_000f));
        Assertions.assertEquals(-0.6452398f, ValueHeuristic.calculateUnbounded(-1_000f));
        Assertions.assertEquals(-0.4995602f, ValueHeuristic.calculateUnbounded(-100f));
        Assertions.assertEquals(-0.30210334f, ValueHeuristic.calculateUnbounded(-10f));
        Assertions.assertEquals(-0.09874952f, ValueHeuristic.calculateUnbounded(-1f));
        Assertions.assertEquals(-0.059007168f, ValueHeuristic.calculateUnbounded(-0.5f));
        Assertions.assertEquals(0f, ValueHeuristic.calculateUnbounded(0f));
        Assertions.assertEquals(0.059007168f, ValueHeuristic.calculateUnbounded(0.5f));
        Assertions.assertEquals(0.09874952f, ValueHeuristic.calculateUnbounded(1f));
        Assertions.assertEquals(0.30210334f, ValueHeuristic.calculateUnbounded(10f));
        Assertions.assertEquals(0.4995602f, ValueHeuristic.calculateUnbounded(100f));
        Assertions.assertEquals(0.6452398f, ValueHeuristic.calculateUnbounded(1_000f));
        Assertions.assertEquals(0.74881506f, ValueHeuristic.calculateUnbounded(10_000f));
        Assertions.assertEquals(0.8221722f, ValueHeuristic.calculateUnbounded(100_000f));
        Assertions.assertEquals(0.8741074f, ValueHeuristic.calculateUnbounded(1_000_000f));
    }

    @Test
    public void TEST_2() {
        Assertions.assertEquals(-1f, ValueHeuristic.convertProbability(0f));
        Assertions.assertEquals(-0.8f, ValueHeuristic.convertProbability(0.1f));
        Assertions.assertEquals(-0.6f, ValueHeuristic.convertProbability(0.2f));
        Assertions.assertEquals(-0.39999998f, ValueHeuristic.convertProbability(0.3f));
        Assertions.assertEquals(-0.19999999f, ValueHeuristic.convertProbability(0.4f));
        Assertions.assertEquals(0f, ValueHeuristic.convertProbability(0.5f));
        Assertions.assertEquals(0.20000005f, ValueHeuristic.convertProbability(0.6f));
        Assertions.assertEquals(0.39999998f, ValueHeuristic.convertProbability(0.7f));
        Assertions.assertEquals(0.6f, ValueHeuristic.convertProbability(0.8f));
        Assertions.assertEquals(0.79999995f, ValueHeuristic.convertProbability(0.9f));
        Assertions.assertEquals(1f, ValueHeuristic.convertProbability(1f));
    }
}
