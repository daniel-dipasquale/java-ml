package com.dipasquale.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class OptimalPairSelectorTest {
    private static final Comparator<Float> FLOAT_ASCENDING_COMPARATOR = Float::compare;
    private static final Comparator<Float> FLOAT_DESCENDING_COMPARATOR = FLOAT_ASCENDING_COMPARATOR.reversed();

    @Test
    public void TEST_1() {
        OptimalPairSelector<Float, String> test = new OptimalPairSelector<>(FLOAT_ASCENDING_COMPARATOR);

        Assertions.assertNull(test.getKey());
        Assertions.assertNull(test.getValue());
    }

    @Test
    public void TEST_2() {
        OptimalPairSelector<Float, String> test = new OptimalPairSelector<>(FLOAT_ASCENDING_COMPARATOR);

        Assertions.assertTrue(test.replaceValueIfBetter(1f, "one"));
        Assertions.assertEquals(1f, test.getKey());
        Assertions.assertEquals("one", test.getValue());
        Assertions.assertFalse(test.replaceValueIfBetter(0.9f, "zero point nine"));
        Assertions.assertEquals(1f, test.getKey());
        Assertions.assertEquals("one", test.getValue());
        Assertions.assertTrue(test.replaceValueIfBetter(1.1f, "one point one"));
        Assertions.assertEquals(1.1f, test.getKey());
        Assertions.assertEquals("one point one", test.getValue());
    }

    @Test
    public void TEST_3() {
        OptimalPairSelector<Float, List<String>> test = new OptimalPairSelector<>(FLOAT_ASCENDING_COMPARATOR);

        Assertions.assertTrue(test.computeValueIfBetter(1f, ArrayList::new));
        test.getValue().add("one a");
        Assertions.assertEquals(1f, test.getKey());
        Assertions.assertEquals(List.of("one a"), test.getValue());
        Assertions.assertFalse(test.computeValueIfBetter(0.9f, ArrayList::new));
        test.getValue().add("zero point nine a");
        Assertions.assertEquals(1f, test.getKey());
        Assertions.assertEquals(List.of("one a", "zero point nine a"), test.getValue());
        Assertions.assertTrue(test.computeValueIfBetter(1.1f, ArrayList::new));
        test.getValue().add("one point one a");
        Assertions.assertEquals(1.1f, test.getKey());
        Assertions.assertEquals(List.of("one point one a"), test.getValue());
    }

    @Test
    public void TEST_4() {
        OptimalPairSelector<Float, String> test = new OptimalPairSelector<>(FLOAT_DESCENDING_COMPARATOR);

        Assertions.assertNull(test.getKey());
        Assertions.assertNull(test.getValue());
    }

    @Test
    public void TEST_5() {
        OptimalPairSelector<Float, String> test = new OptimalPairSelector<>(FLOAT_DESCENDING_COMPARATOR);

        Assertions.assertTrue(test.replaceValueIfBetter(1f, "one"));
        Assertions.assertEquals(1f, test.getKey());
        Assertions.assertEquals("one", test.getValue());
        Assertions.assertTrue(test.replaceValueIfBetter(0.9f, "zero point nine"));
        Assertions.assertEquals(0.9f, test.getKey());
        Assertions.assertEquals("zero point nine", test.getValue());
        Assertions.assertFalse(test.replaceValueIfBetter(1.1f, "one point one"));
        Assertions.assertEquals(0.9f, test.getKey());
        Assertions.assertEquals("zero point nine", test.getValue());
    }

    @Test
    public void TEST_6() {
        OptimalPairSelector<Float, List<String>> test = new OptimalPairSelector<>(FLOAT_DESCENDING_COMPARATOR);

        Assertions.assertTrue(test.computeValueIfBetter(1f, ArrayList::new));
        test.getValue().add("one a");
        Assertions.assertEquals(1f, test.getKey());
        Assertions.assertEquals(List.of("one a"), test.getValue());
        Assertions.assertTrue(test.computeValueIfBetter(0.9f, ArrayList::new));
        test.getValue().add("zero point nine a");
        Assertions.assertEquals(0.9f, test.getKey());
        Assertions.assertEquals(List.of("zero point nine a"), test.getValue());
        Assertions.assertFalse(test.computeValueIfBetter(1.1f, ArrayList::new));
        test.getValue().add("one point one a");
        Assertions.assertEquals(0.9f, test.getKey());
        Assertions.assertEquals(List.of("zero point nine a", "one point one a"), test.getValue());
    }
}
