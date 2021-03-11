package com.dipasquale.data.structure.deque;

import org.junit.Assert;
import org.junit.Test;

public final class NodeDequeTest {
    @Test
    public void TEST_1() {
        NodeDeque<String> test = NodeDeque.create();

        Assert.assertTrue(test instanceof NodeDequeDefault<String>);
    }
}
