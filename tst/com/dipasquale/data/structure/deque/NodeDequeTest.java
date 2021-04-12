package com.dipasquale.data.structure.deque;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class NodeDequeTest {
    @Test
    public void TEST_1() {
        NodeDeque<String, SimpleNode<String>> result = NodeDeque.createSynchronized(new SimpleNodeDeque<>());

        Assertions.assertTrue(result instanceof NodeDequeSynchronized);
    }
}
