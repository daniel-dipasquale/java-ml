package com.dipasquale.data.structure.deque;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StandardNodeTest {
    @Test
    public void TEST_0() {
        Object membership = new Object();
        StandardNode<String> test = new StandardNode<>(membership, "value-1");

        Assertions.assertEquals(membership, test.membership);
        Assertions.assertEquals(new StandardNode<>(membership, "value-1"), test);
        Assertions.assertEquals(new StandardNode<>(new Object(), "value-1"), test);
        Assertions.assertEquals(new StandardNode<>(null, "value-1"), test);
        Assertions.assertNotEquals(new StandardNode<>(null, "value-2"), test);
        Assertions.assertEquals("StandardNode(value=value-1)", test.toString());
    }
}
