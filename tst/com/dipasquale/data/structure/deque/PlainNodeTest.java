package com.dipasquale.data.structure.deque;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PlainNodeTest {
    @Test
    public void TEST_0() {
        Object membership = new Object();
        PlainNode<String> test = new PlainNode<>(membership, "value-1");

        Assertions.assertEquals(membership, test.membership);
        Assertions.assertEquals(new PlainNode<>(membership, "value-1"), test);
        Assertions.assertEquals(new PlainNode<>(new Object(), "value-1"), test);
        Assertions.assertEquals(new PlainNode<>(null, "value-1"), test);
        Assertions.assertNotEquals(new PlainNode<>(null, "value-2"), test);
        Assertions.assertEquals("PlainNode(value=value-1)", test.toString());
    }
}
