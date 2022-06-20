package com.dipasquale.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class OptimalPairScoutTest {
    private static final Comparator<Float> FLOAT_ASCENDING_COMPARATOR = Float::compare;
    private static final Comparator<Float> FLOAT_DESCENDING_COMPARATOR = FLOAT_ASCENDING_COMPARATOR.reversed();

    @Test
    public void TEST_1() {
        OptimalPairScout<Float, String> test = new OptimalPairScout<>(FLOAT_ASCENDING_COMPARATOR);

        Assertions.assertNull(test.getRanking());
        Assertions.assertNull(test.getValue());
    }

    @Test
    public void TEST_2() {
        OptimalPairScout<Float, String> test = new OptimalPairScout<>(FLOAT_ASCENDING_COMPARATOR);

        Assertions.assertTrue(test.replaceIfHigherRanking(1f, "one"));
        Assertions.assertEquals(1f, test.getRanking());
        Assertions.assertEquals("one", test.getValue());
        Assertions.assertFalse(test.replaceIfHigherRanking(0.9f, "zero point nine"));
        Assertions.assertEquals(1f, test.getRanking());
        Assertions.assertEquals("one", test.getValue());
        Assertions.assertTrue(test.replaceIfHigherRanking(1.1f, "one point one"));
        Assertions.assertEquals(1.1f, test.getRanking());
        Assertions.assertEquals("one point one", test.getValue());
    }

    @Test
    public void TEST_3() {
        OptimalPairScout<Float, List<String>> test = new OptimalPairScout<>(FLOAT_ASCENDING_COMPARATOR);

        Assertions.assertTrue(test.computeIfHigherRanking(1f, ArrayList::new));
        test.getValue().add("one a");
        Assertions.assertEquals(1f, test.getRanking());
        Assertions.assertEquals(List.of("one a"), test.getValue());
        Assertions.assertFalse(test.computeIfHigherRanking(0.9f, ArrayList::new));
        test.getValue().add("zero point nine a");
        Assertions.assertEquals(1f, test.getRanking());
        Assertions.assertEquals(List.of("one a", "zero point nine a"), test.getValue());
        Assertions.assertTrue(test.computeIfHigherRanking(1.1f, ArrayList::new));
        test.getValue().add("one point one a");
        Assertions.assertEquals(1.1f, test.getRanking());
        Assertions.assertEquals(List.of("one point one a"), test.getValue());
    }

    @Test
    public void TEST_4() {
        OptimalPairScout<Float, String> test = new OptimalPairScout<>(FLOAT_DESCENDING_COMPARATOR);

        Assertions.assertNull(test.getRanking());
        Assertions.assertNull(test.getValue());
    }

    @Test
    public void TEST_5() {
        OptimalPairScout<Float, String> test = new OptimalPairScout<>(FLOAT_DESCENDING_COMPARATOR);

        Assertions.assertTrue(test.replaceIfHigherRanking(1f, "one"));
        Assertions.assertEquals(1f, test.getRanking());
        Assertions.assertEquals("one", test.getValue());
        Assertions.assertTrue(test.replaceIfHigherRanking(0.9f, "zero point nine"));
        Assertions.assertEquals(0.9f, test.getRanking());
        Assertions.assertEquals("zero point nine", test.getValue());
        Assertions.assertFalse(test.replaceIfHigherRanking(1.1f, "one point one"));
        Assertions.assertEquals(0.9f, test.getRanking());
        Assertions.assertEquals("zero point nine", test.getValue());
    }

    @Test
    public void TEST_6() {
        OptimalPairScout<Float, List<String>> test = new OptimalPairScout<>(FLOAT_DESCENDING_COMPARATOR);

        Assertions.assertTrue(test.computeIfHigherRanking(1f, ArrayList::new));
        test.getValue().add("one a");
        Assertions.assertEquals(1f, test.getRanking());
        Assertions.assertEquals(List.of("one a"), test.getValue());
        Assertions.assertTrue(test.computeIfHigherRanking(0.9f, ArrayList::new));
        test.getValue().add("zero point nine a");
        Assertions.assertEquals(0.9f, test.getRanking());
        Assertions.assertEquals(List.of("zero point nine a"), test.getValue());
        Assertions.assertFalse(test.computeIfHigherRanking(1.1f, ArrayList::new));
        test.getValue().add("one point one a");
        Assertions.assertEquals(0.9f, test.getRanking());
        Assertions.assertEquals(List.of("zero point nine a", "one point one a"), test.getValue());
    }
}
