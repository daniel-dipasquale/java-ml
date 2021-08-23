package com.dipasquale.ai.common;

import com.dipasquale.ai.common.output.OutputClassifier;
import com.dipasquale.common.error.ErrorComparer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

public final class OutputClassifierTest {
    @Test
    public void TEST_1() {
        OutputClassifier<Integer> test = new OutputClassifier<>();

        test.addUpUntil(0, 0.25f);
        test.addUpUntil(1, 0.5f);
        test.addOtherwiseRoundedUp(2);

        Assertions.assertEquals(0, test.resolve(0f));
        Assertions.assertEquals(0, test.resolve(0.249f));
        Assertions.assertEquals(1, test.resolve(0.25f));
        Assertions.assertEquals(1, test.resolve(0.749f));
        Assertions.assertEquals(2, test.resolve(0.75f));
        Assertions.assertEquals(2, test.resolve(0.99f));

        try {
            test.resolve(1f);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparer.builder()
                    .type(NoSuchElementException.class)
                    .message("1.000000 is out of range")
                    .build(), ErrorComparer.create(e));
        }
    }

    @Test
    public void TEST_2() {
        OutputClassifier<Integer> test = new OutputClassifier<>();

        test.addUpUntil(0, 0f);
        test.addUpUntil(1, 0f);
        test.addOtherwiseRoundedUp(2);

        Assertions.assertEquals(2, test.resolve(0f));
        Assertions.assertEquals(2, test.resolve(0.249f));
        Assertions.assertEquals(2, test.resolve(0.25f));
        Assertions.assertEquals(2, test.resolve(0.749f));
        Assertions.assertEquals(2, test.resolve(0.75f));
        Assertions.assertEquals(2, test.resolve(0.99f));

        try {
            test.resolve(1f);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparer.builder()
                    .type(NoSuchElementException.class)
                    .message("1.000000 is out of range")
                    .build(), ErrorComparer.create(e));
        }
    }
}
