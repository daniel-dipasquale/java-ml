/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.deque;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimpleNodeTest {
    @Test
    public void TEST_0() {
        Object membership = new Object();
        SimpleNode<String> test = new SimpleNode<>("value-1", membership);

        Assertions.assertEquals(membership, test.membership);
        Assertions.assertEquals(new SimpleNode<>("value-1", membership), test);
        Assertions.assertEquals(new SimpleNode<>("value-1", new Object()), test);
        Assertions.assertEquals(new SimpleNode<>("value-1", null), test);
        Assertions.assertNotEquals(new SimpleNode<>("value-2", null), test);
        Assertions.assertEquals("SimpleNode(value=value-1)", test.toString());
    }
}
