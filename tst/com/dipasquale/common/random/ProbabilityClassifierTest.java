package com.dipasquale.common.random;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class ProbabilityClassifierTest {
    @Test
    public void TEST_1() {
        ProbabilityClassifier<ClassificationType> test = new ProbabilityClassifier<>();

        Assertions.assertTrue(test.addProbabilityFor(0.25f, ClassificationType.ITEM_A));
        Assertions.assertTrue(test.addProbabilityFor(0.5f, ClassificationType.ITEM_B));
        Assertions.assertTrue(test.addRemainingProbabilityFor(ClassificationType.ITEM_C));
        Assertions.assertNull(test.get(-Float.MAX_VALUE));
        Assertions.assertEquals(ClassificationType.ITEM_A, test.get(0f));
        Assertions.assertEquals(ClassificationType.ITEM_A, test.get(0.249f));
        Assertions.assertEquals(ClassificationType.ITEM_B, test.get(0.25f));
        Assertions.assertEquals(ClassificationType.ITEM_B, test.get(0.749f));
        Assertions.assertEquals(ClassificationType.ITEM_C, test.get(0.75f));
        Assertions.assertEquals(ClassificationType.ITEM_C, test.get(0.99f));
        Assertions.assertNull(test.get(1f));
    }

    @Test
    public void TEST_2() {
        ProbabilityClassifier<ClassificationType> test = new ProbabilityClassifier<>();

        Assertions.assertFalse(test.addProbabilityFor(0f, ClassificationType.ITEM_A));
        Assertions.assertFalse(test.addProbabilityFor(0f, ClassificationType.ITEM_B));
        Assertions.assertTrue(test.addRemainingProbabilityFor(ClassificationType.ITEM_C));
        Assertions.assertNull(test.get(-Float.MAX_VALUE));
        Assertions.assertEquals(ClassificationType.ITEM_C, test.get(0f));
        Assertions.assertEquals(ClassificationType.ITEM_C, test.get(0.249f));
        Assertions.assertEquals(ClassificationType.ITEM_C, test.get(0.25f));
        Assertions.assertEquals(ClassificationType.ITEM_C, test.get(0.749f));
        Assertions.assertEquals(ClassificationType.ITEM_C, test.get(0.75f));
        Assertions.assertEquals(ClassificationType.ITEM_C, test.get(0.99f));
        Assertions.assertNull(test.get(1f));
    }

    @Test
    public void TEST_3() {
        ProbabilityClassifier<ClassificationType> test = new ProbabilityClassifier<>();

        Assertions.assertTrue(test.addProbabilityFor(1f, ClassificationType.ITEM_A));
        Assertions.assertFalse(test.addProbabilityFor(0.5f, ClassificationType.ITEM_B));
        Assertions.assertFalse(test.addRemainingProbabilityFor(ClassificationType.ITEM_C));
        Assertions.assertNull(test.get(-Float.MAX_VALUE));
        Assertions.assertEquals(ClassificationType.ITEM_A, test.get(0f));
        Assertions.assertEquals(ClassificationType.ITEM_A, test.get(0.249f));
        Assertions.assertEquals(ClassificationType.ITEM_A, test.get(0.25f));
        Assertions.assertEquals(ClassificationType.ITEM_A, test.get(0.749f));
        Assertions.assertEquals(ClassificationType.ITEM_A, test.get(0.75f));
        Assertions.assertEquals(ClassificationType.ITEM_A, test.get(0.99f));
        Assertions.assertNull(test.get(1f));
    }

    @Test
    public void TEST_4() {
        ProbabilityClassifier<ClassificationType> test = new ProbabilityClassifier<>();

        Assertions.assertTrue(test.addRemainingProbabilityFor(ClassificationType.ITEM_A));
        Assertions.assertFalse(test.addRemainingProbabilityFor(ClassificationType.ITEM_B));
        Assertions.assertNull(test.get(-Float.MAX_VALUE));
        Assertions.assertEquals(ClassificationType.ITEM_A, test.get(0f));
        Assertions.assertEquals(ClassificationType.ITEM_A, test.get(0.249f));
        Assertions.assertEquals(ClassificationType.ITEM_A, test.get(0.25f));
        Assertions.assertEquals(ClassificationType.ITEM_A, test.get(0.749f));
        Assertions.assertEquals(ClassificationType.ITEM_A, test.get(0.75f));
        Assertions.assertEquals(ClassificationType.ITEM_A, test.get(0.99f));
        Assertions.assertNull(test.get(1f));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum ClassificationType {
        ITEM_A,
        ITEM_B,
        ITEM_C
    }
}
