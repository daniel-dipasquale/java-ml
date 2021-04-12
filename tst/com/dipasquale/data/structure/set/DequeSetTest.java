package com.dipasquale.data.structure.set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class DequeSetTest {
    @Test
    public void TEST_1() {
        DequeSet<String> result = DequeSet.createSynchronized(new HashDequeSet<>());

        Assertions.assertTrue(result instanceof DequeSetSynchronized);
    }
}
