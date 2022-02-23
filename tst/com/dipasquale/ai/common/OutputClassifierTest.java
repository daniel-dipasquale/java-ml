package com.dipasquale.ai.common;

import com.dipasquale.ai.common.output.OutputClassifier;
import com.dipasquale.common.error.ErrorComparator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

public final class OutputClassifierTest {
    @Test
    public void TEST_1() {
        OutputClassifier<Classification> test = new OutputClassifier<>();

        test.addRangeFor(0.25f, Classification.A);
        test.addRangeFor(0.5f, Classification.B);
        test.addRemainingRangeFor(Classification.C);

        Assertions.assertEquals(Classification.A, test.resolve(-Float.MAX_VALUE));
        Assertions.assertEquals(Classification.A, test.resolve(0f));
        Assertions.assertEquals(Classification.A, test.resolve(0.249f));
        Assertions.assertEquals(Classification.B, test.resolve(0.25f));
        Assertions.assertEquals(Classification.B, test.resolve(0.749f));
        Assertions.assertEquals(Classification.C, test.resolve(0.75f));
        Assertions.assertEquals(Classification.C, test.resolve(0.99f));

        try {
            test.resolve(1f);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparator.builder()
                    .type(NoSuchElementException.class)
                    .message("1.000000 is out of range")
                    .build(), ErrorComparator.create(e));
        }
    }

    @Test
    public void TEST_2() {
        OutputClassifier<Classification> test = new OutputClassifier<>();

        test.addRangeFor(0f, Classification.A);
        test.addRangeFor(0f, Classification.B);
        test.addRemainingRangeFor(Classification.C);

        Assertions.assertEquals(Classification.C, test.resolve(-Float.MAX_VALUE));
        Assertions.assertEquals(Classification.C, test.resolve(0f));
        Assertions.assertEquals(Classification.C, test.resolve(0.249f));
        Assertions.assertEquals(Classification.C, test.resolve(0.25f));
        Assertions.assertEquals(Classification.C, test.resolve(0.749f));
        Assertions.assertEquals(Classification.C, test.resolve(0.75f));
        Assertions.assertEquals(Classification.C, test.resolve(0.99f));

        try {
            test.resolve(1f);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparator.builder()
                    .type(NoSuchElementException.class)
                    .message("1.000000 is out of range")
                    .build(), ErrorComparator.create(e));
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum Classification {
        A,
        B,
        C
    }
}
