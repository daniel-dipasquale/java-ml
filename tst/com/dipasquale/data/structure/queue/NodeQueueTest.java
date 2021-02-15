package com.dipasquale.data.structure.queue;

import org.junit.Assert;
import org.junit.Test;

public final class NodeQueueTest {
    @Test
    public void TEST_1() {
        NodeQueue<String> test = NodeQueue.create();

        Assert.assertTrue(test instanceof NodeQueueDefault<String>);
    }
}
