package com.dipasquale.data.structure.deque;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimpleNodeTest {
    @Test
    public void TEST_0() {
        Object membership = new Object();
        SimpleNode<String> test = new SimpleNode<>(membership, "value-1");

        Assertions.assertEquals(membership, test.membership);
        Assertions.assertEquals(new SimpleNode<>(membership, "value-1"), test);
        Assertions.assertEquals(new SimpleNode<>(new Object(), "value-1"), test);
        Assertions.assertEquals(new SimpleNode<>(null, "value-1"), test);
        Assertions.assertNotEquals(new SimpleNode<>(null, "value-2"), test);
        Assertions.assertEquals("SimpleNode(value=value-1)", test.toString());
    }
}
