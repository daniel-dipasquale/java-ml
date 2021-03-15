package com.dipasquale.data.structure.deque;

import org.junit.Assert;
import org.junit.Test;

public class SimpleNodeTest {
    @Test
    public void TEST_0() {
        Object membership = new Object();
        SimpleNode<String> test = new SimpleNode<>("value-1", membership);

        Assert.assertEquals(membership, test.membership);
        Assert.assertEquals(new SimpleNode<>("value-1", membership), test);
        Assert.assertEquals(new SimpleNode<>("value-1", new Object()), test);
        Assert.assertEquals(new SimpleNode<>("value-1", null), test);
        Assert.assertNotEquals(new SimpleNode<>("value-2", null), test);
        Assert.assertEquals("SimpleNode(value=value-1)", test.toString());
    }
}
