package com.dipasquale.data.structure.deque;

import org.junit.Assert;
import org.junit.Test;

public final class NodeDefaultTest {
    @Test
    public void TEST_1() {
        Object membership = new Object();
        NodeDefault<String> test = new NodeDefault<>("value-1", membership);

        Assert.assertEquals(membership, test.getMembership());
        Assert.assertEquals(new NodeDefault<>("value-1", membership), test);
        Assert.assertEquals(new NodeDefault<>("value-1", new Object()), test);
        Assert.assertEquals(new NodeDefault<>("value-1", null), test);
        Assert.assertNotEquals(new NodeDefault<>("value-2", null), test);
        Assert.assertEquals("NodeDefault(value=value-1)", test.toString());
    }
}
