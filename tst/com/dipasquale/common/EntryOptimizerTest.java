package com.dipasquale.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class EntryOptimizerTest {
    private static final Comparator<Float> FLOAT_COMPARATOR_ASCENDING = Float::compare;
    private static final Comparator<Float> FLOAT_COMPARATOR_DESCENDING = FLOAT_COMPARATOR_ASCENDING.reversed();

    @Test
    public void TEST_1() {
        EntryOptimizer<Float, String> test = new EntryOptimizer<>(FLOAT_COMPARATOR_ASCENDING);

        Assertions.assertNull(test.getKey());
        Assertions.assertNull(test.getValue());
    }

    @Test
    public void TEST_2() {
        EntryOptimizer<Float, String> test = new EntryOptimizer<>(FLOAT_COMPARATOR_ASCENDING);

        Assertions.assertTrue(test.replaceValueIfMoreOptimum(1f, "one"));
        Assertions.assertEquals(1f, test.getKey());
        Assertions.assertEquals("one", test.getValue());
        Assertions.assertFalse(test.replaceValueIfMoreOptimum(0.9f, "zero point nine"));
        Assertions.assertEquals(1f, test.getKey());
        Assertions.assertEquals("one", test.getValue());
        Assertions.assertTrue(test.replaceValueIfMoreOptimum(1.1f, "one point one"));
        Assertions.assertEquals(1.1f, test.getKey());
        Assertions.assertEquals("one point one", test.getValue());
    }

    @Test
    public void TEST_3() {
        EntryOptimizer<Float, List<String>> test = new EntryOptimizer<>(FLOAT_COMPARATOR_ASCENDING);

        Assertions.assertTrue(test.computeValueIfMoreOptimum(1f, ArrayList::new));
        test.getValue().add("one a");
        Assertions.assertEquals(1f, test.getKey());
        Assertions.assertEquals(List.of("one a"), test.getValue());
        Assertions.assertFalse(test.computeValueIfMoreOptimum(0.9f, ArrayList::new));
        test.getValue().add("zero point nine a");
        Assertions.assertEquals(1f, test.getKey());
        Assertions.assertEquals(List.of("one a", "zero point nine a"), test.getValue());
        Assertions.assertTrue(test.computeValueIfMoreOptimum(1.1f, ArrayList::new));
        test.getValue().add("one point one a");
        Assertions.assertEquals(1.1f, test.getKey());
        Assertions.assertEquals(List.of("one point one a"), test.getValue());
    }

    @Test
    public void TEST_4() {
        EntryOptimizer<Float, String> test = new EntryOptimizer<>(FLOAT_COMPARATOR_DESCENDING);

        Assertions.assertNull(test.getKey());
        Assertions.assertNull(test.getValue());
    }

    @Test
    public void TEST_5() {
        EntryOptimizer<Float, String> test = new EntryOptimizer<>(FLOAT_COMPARATOR_DESCENDING);

        Assertions.assertTrue(test.replaceValueIfMoreOptimum(1f, "one"));
        Assertions.assertEquals(1f, test.getKey());
        Assertions.assertEquals("one", test.getValue());
        Assertions.assertTrue(test.replaceValueIfMoreOptimum(0.9f, "zero point nine"));
        Assertions.assertEquals(0.9f, test.getKey());
        Assertions.assertEquals("zero point nine", test.getValue());
        Assertions.assertFalse(test.replaceValueIfMoreOptimum(1.1f, "one point one"));
        Assertions.assertEquals(0.9f, test.getKey());
        Assertions.assertEquals("zero point nine", test.getValue());
    }

    @Test
    public void TEST_6() {
        EntryOptimizer<Float, List<String>> test = new EntryOptimizer<>(FLOAT_COMPARATOR_DESCENDING);

        Assertions.assertTrue(test.computeValueIfMoreOptimum(1f, ArrayList::new));
        test.getValue().add("one a");
        Assertions.assertEquals(1f, test.getKey());
        Assertions.assertEquals(List.of("one a"), test.getValue());
        Assertions.assertTrue(test.computeValueIfMoreOptimum(0.9f, ArrayList::new));
        test.getValue().add("zero point nine a");
        Assertions.assertEquals(0.9f, test.getKey());
        Assertions.assertEquals(List.of("zero point nine a"), test.getValue());
        Assertions.assertFalse(test.computeValueIfMoreOptimum(1.1f, ArrayList::new));
        test.getValue().add("one point one a");
        Assertions.assertEquals(0.9f, test.getKey());
        Assertions.assertEquals(List.of("zero point nine a", "one point one a"), test.getValue());
    }
}
