package com.dipasquale.simulation.game2048;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public final class OptimumValuedTileTest {
    private static final OptimumValuedTile TEST = OptimumValuedTile.getInstance();

    @Test
    public void TEST_1() {
        Assertions.assertEquals(List.of(), TEST.getMaximumValuedTiles(0));
        Assertions.assertEquals(List.of(2, 2), TEST.getMaximumValuedTiles(1));
        Assertions.assertEquals(List.of(3), TEST.getMaximumValuedTiles(2));
        Assertions.assertEquals(List.of(3, 2), TEST.getMaximumValuedTiles(3));
        Assertions.assertEquals(List.of(3, 2), TEST.getMaximumValuedTiles(4));
        Assertions.assertEquals(List.of(3, 2, 2), TEST.getMaximumValuedTiles(5));
        Assertions.assertEquals(List.of(3, 3), TEST.getMaximumValuedTiles(6));
        Assertions.assertEquals(List.of(3, 3, 2), TEST.getMaximumValuedTiles(7));
        Assertions.assertEquals(List.of(4, 2), TEST.getMaximumValuedTiles(8));
        Assertions.assertEquals(List.of(4, 2, 2), TEST.getMaximumValuedTiles(9));
        Assertions.assertEquals(List.of(4, 3), TEST.getMaximumValuedTiles(10));
        Assertions.assertEquals(List.of(4, 3, 2), TEST.getMaximumValuedTiles(11));
        Assertions.assertEquals(List.of(4, 3, 2), TEST.getMaximumValuedTiles(12));
        Assertions.assertEquals(List.of(4, 3, 2, 2), TEST.getMaximumValuedTiles(13));
        Assertions.assertEquals(List.of(4, 3, 3), TEST.getMaximumValuedTiles(14));
        Assertions.assertEquals(List.of(4, 3, 3, 2), TEST.getMaximumValuedTiles(15));
        Assertions.assertEquals(List.of(4, 4, 2), TEST.getMaximumValuedTiles(16));
        Assertions.assertEquals(List.of(4, 4, 2, 2), TEST.getMaximumValuedTiles(17));
        Assertions.assertEquals(List.of(5, 3), TEST.getMaximumValuedTiles(18));
        Assertions.assertEquals(List.of(5, 3, 2), TEST.getMaximumValuedTiles(19));
        Assertions.assertEquals(List.of(5, 3, 2), TEST.getMaximumValuedTiles(20));
        Assertions.assertEquals(List.of(5, 3, 2, 2), TEST.getMaximumValuedTiles(21));
        Assertions.assertEquals(List.of(5, 3, 3), TEST.getMaximumValuedTiles(22));
        Assertions.assertEquals(List.of(5, 3, 3, 2), TEST.getMaximumValuedTiles(23));
        Assertions.assertEquals(List.of(5, 4, 2), TEST.getMaximumValuedTiles(24));
        Assertions.assertEquals(List.of(5, 4, 2, 2), TEST.getMaximumValuedTiles(25));
        Assertions.assertEquals(List.of(5, 4, 3), TEST.getMaximumValuedTiles(26));
        Assertions.assertEquals(List.of(5, 4, 3, 2), TEST.getMaximumValuedTiles(27));
        Assertions.assertEquals(List.of(5, 4, 3, 2), TEST.getMaximumValuedTiles(28));
        Assertions.assertEquals(List.of(5, 4, 3, 2, 2), TEST.getMaximumValuedTiles(29));
        Assertions.assertEquals(List.of(5, 4, 3, 3), TEST.getMaximumValuedTiles(30));
        Assertions.assertEquals(List.of(5, 4, 3, 3, 2), TEST.getMaximumValuedTiles(31));
        Assertions.assertEquals(List.of(5, 4, 4, 2), TEST.getMaximumValuedTiles(32));
        Assertions.assertEquals(List.of(5, 4, 4, 2, 2), TEST.getMaximumValuedTiles(33));
        Assertions.assertEquals(List.of(5, 5, 3), TEST.getMaximumValuedTiles(34));
        Assertions.assertEquals(List.of(5, 5, 3, 2), TEST.getMaximumValuedTiles(35));
        Assertions.assertEquals(List.of(6, 3, 2), TEST.getMaximumValuedTiles(36));
        Assertions.assertEquals(List.of(6, 3, 2, 2), TEST.getMaximumValuedTiles(37));
        Assertions.assertEquals(List.of(6, 3, 3), TEST.getMaximumValuedTiles(38));
        Assertions.assertEquals(List.of(6, 3, 3, 2), TEST.getMaximumValuedTiles(39));
        Assertions.assertEquals(List.of(6, 4, 2), TEST.getMaximumValuedTiles(40));
        Assertions.assertEquals(List.of(6, 4, 2, 2), TEST.getMaximumValuedTiles(41));
        Assertions.assertEquals(List.of(6, 4, 3), TEST.getMaximumValuedTiles(42));
        Assertions.assertEquals(List.of(6, 4, 3, 2), TEST.getMaximumValuedTiles(43));
        Assertions.assertEquals(List.of(6, 4, 3, 2), TEST.getMaximumValuedTiles(44));
        Assertions.assertEquals(List.of(6, 4, 3, 2, 2), TEST.getMaximumValuedTiles(45));
        Assertions.assertEquals(List.of(6, 4, 3, 3), TEST.getMaximumValuedTiles(46));
        Assertions.assertEquals(List.of(6, 4, 3, 3, 2), TEST.getMaximumValuedTiles(47));
        Assertions.assertEquals(List.of(6, 4, 4, 2), TEST.getMaximumValuedTiles(48));
        Assertions.assertEquals(List.of(6, 4, 4, 2, 2), TEST.getMaximumValuedTiles(49));
        Assertions.assertEquals(List.of(10, 9, 8, 7, 6, 4, 2), TEST.getMaximumValuedTiles(1_000));
        Assertions.assertEquals(List.of(11, 10, 9, 8, 7, 4, 4, 2), TEST.getMaximumValuedTiles(2_000));
        Assertions.assertEquals(List.of(12, 11, 10, 9, 8, 5, 4, 4, 2), TEST.getMaximumValuedTiles(4_000));
        Assertions.assertEquals(List.of(13, 12, 11, 10, 9, 6, 5, 4, 4, 2), TEST.getMaximumValuedTiles(8_000));
        Assertions.assertEquals(List.of(14, 13, 12, 11, 10, 7, 6, 5, 4, 4, 2), TEST.getMaximumValuedTiles(16_000));
        Assertions.assertEquals(List.of(15, 14, 13, 12, 11, 8, 7, 6, 5, 4, 4, 2), TEST.getMaximumValuedTiles(32_000));
        Assertions.assertEquals(List.of(16, 15, 14, 13, 12, 9, 8, 7, 6, 5, 4, 4, 2), TEST.getMaximumValuedTiles(64_000));
    }

    @Test
    public void TEST_2() {
        Assertions.assertEquals(List.of(4, 3, 3), TEST.getMaximumValuedTiles(15, 3));
        Assertions.assertEquals(List.of(4, 3, 3, 2), TEST.getMaximumValuedTiles(15, 4));
        Assertions.assertEquals(List.of(4, 3, 3, 1, 1), TEST.getMaximumValuedTiles(15, 5));
        Assertions.assertEquals(List.of(4, 3, 2, 2, 1, 1), TEST.getMaximumValuedTiles(15, 6));
        Assertions.assertEquals(List.of(4, 3, 2, 1, 1, 1, 1), TEST.getMaximumValuedTiles(15, 7));
        Assertions.assertEquals(List.of(4, 3, 1, 1, 1, 1, 1, 1), TEST.getMaximumValuedTiles(15, 8));
        Assertions.assertEquals(List.of(4, 2, 2, 1, 1, 1, 1, 1, 1), TEST.getMaximumValuedTiles(15, 9));
        Assertions.assertEquals(List.of(4, 2, 1, 1, 1, 1, 1, 1, 1, 1), TEST.getMaximumValuedTiles(15, 10));
        Assertions.assertEquals(List.of(4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1), TEST.getMaximumValuedTiles(15, 11));
        Assertions.assertEquals(List.of(3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1), TEST.getMaximumValuedTiles(15, 12));
        Assertions.assertEquals(List.of(3, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1), TEST.getMaximumValuedTiles(15, 13));
        Assertions.assertEquals(List.of(3, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1), TEST.getMaximumValuedTiles(15, 14));
        Assertions.assertEquals(List.of(3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1), TEST.getMaximumValuedTiles(15, 15));
        Assertions.assertEquals(List.of(2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1), TEST.getMaximumValuedTiles(15, 16));
        Assertions.assertEquals(List.of(10, 9, 8, 7, 6, 4), TEST.getMaximumValuedTiles(1_000, 6));
        Assertions.assertEquals(List.of(10, 9, 8, 7, 6, 4, 2), TEST.getMaximumValuedTiles(1_000, 7));
        Assertions.assertEquals(List.of(10, 9, 8, 7, 6, 4, 1, 1), TEST.getMaximumValuedTiles(1_000, 8));
        Assertions.assertEquals(List.of(10, 9, 8, 7, 6, 3, 3, 1, 1), TEST.getMaximumValuedTiles(1_000, 9));
        Assertions.assertEquals(List.of(10, 9, 8, 7, 6, 3, 2, 2, 1, 1), TEST.getMaximumValuedTiles(1_000, 10));
        Assertions.assertEquals(List.of(10, 9, 8, 7, 6, 3, 2, 1, 1, 1, 1), TEST.getMaximumValuedTiles(1_000, 11));
        Assertions.assertEquals(List.of(10, 9, 8, 7, 6, 3, 1, 1, 1, 1, 1, 1), TEST.getMaximumValuedTiles(1_000, 12));
        Assertions.assertEquals(List.of(10, 9, 8, 7, 6, 2, 2, 1, 1, 1, 1, 1, 1), TEST.getMaximumValuedTiles(1_000, 13));
        Assertions.assertEquals(List.of(10, 9, 8, 7, 6, 2, 1, 1, 1, 1, 1, 1, 1, 1), TEST.getMaximumValuedTiles(1_000, 14));
        Assertions.assertEquals(List.of(10, 9, 8, 7, 6, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1), TEST.getMaximumValuedTiles(1_000, 15));
        Assertions.assertEquals(List.of(10, 9, 8, 7, 5, 5, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1), TEST.getMaximumValuedTiles(1_000, 16));
    }
}
