package com.dipasquale.search.mcts.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class RewardHeuristicTest {
    @Test
    public void TEST_1() {
        Assertions.assertEquals(-0.8741074f, RewardHeuristic.calculateUnbounded(-1_000_000f));
        Assertions.assertEquals(-0.8221722f, RewardHeuristic.calculateUnbounded(-100_000f));
        Assertions.assertEquals(-0.74881506f, RewardHeuristic.calculateUnbounded(-10_000f));
        Assertions.assertEquals(-0.6452398f, RewardHeuristic.calculateUnbounded(-1_000f));
        Assertions.assertEquals(-0.4995602f, RewardHeuristic.calculateUnbounded(-100f));
        Assertions.assertEquals(-0.30210334f, RewardHeuristic.calculateUnbounded(-10f));
        Assertions.assertEquals(-0.09874952f, RewardHeuristic.calculateUnbounded(-1f));
        Assertions.assertEquals(-0.059007168f, RewardHeuristic.calculateUnbounded(-0.5f));
        Assertions.assertEquals(0f, RewardHeuristic.calculateUnbounded(0f));
        Assertions.assertEquals(0.059007168f, RewardHeuristic.calculateUnbounded(0.5f));
        Assertions.assertEquals(0.09874952f, RewardHeuristic.calculateUnbounded(1f));
        Assertions.assertEquals(0.30210334f, RewardHeuristic.calculateUnbounded(10f));
        Assertions.assertEquals(0.4995602f, RewardHeuristic.calculateUnbounded(100f));
        Assertions.assertEquals(0.6452398f, RewardHeuristic.calculateUnbounded(1_000f));
        Assertions.assertEquals(0.74881506f, RewardHeuristic.calculateUnbounded(10_000f));
        Assertions.assertEquals(0.8221722f, RewardHeuristic.calculateUnbounded(100_000f));
        Assertions.assertEquals(0.8741074f, RewardHeuristic.calculateUnbounded(1_000_000f));
    }

    @Test
    public void TEST_2() {
        Assertions.assertEquals(-1f, RewardHeuristic.convertProbability(0f));
        Assertions.assertEquals(-0.8f, RewardHeuristic.convertProbability(0.1f));
        Assertions.assertEquals(-0.6f, RewardHeuristic.convertProbability(0.2f));
        Assertions.assertEquals(-0.39999998f, RewardHeuristic.convertProbability(0.3f));
        Assertions.assertEquals(-0.19999999f, RewardHeuristic.convertProbability(0.4f));
        Assertions.assertEquals(0f, RewardHeuristic.convertProbability(0.5f));
        Assertions.assertEquals(0.20000005f, RewardHeuristic.convertProbability(0.6f));
        Assertions.assertEquals(0.39999998f, RewardHeuristic.convertProbability(0.7f));
        Assertions.assertEquals(0.6f, RewardHeuristic.convertProbability(0.8f));
        Assertions.assertEquals(0.79999995f, RewardHeuristic.convertProbability(0.9f));
        Assertions.assertEquals(1f, RewardHeuristic.convertProbability(1f));
    }
}
