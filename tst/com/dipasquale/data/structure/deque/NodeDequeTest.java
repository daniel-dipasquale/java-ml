package com.dipasquale.data.structure.deque;

import org.junit.Assert;
import org.junit.Test;

public final class NodeDequeTest {
    @Test
    public void TEST_1() {
        NodeDeque<String, SimpleNode<String>> result = NodeDeque.createSynchronized(new SimpleNodeDeque<>());

        Assert.assertTrue(result instanceof NodeDequeSynchronized<String, SimpleNode<String>>);
    }
}
