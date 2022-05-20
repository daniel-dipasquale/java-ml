package com.dipasquale.search.mcts;

import com.dipasquale.data.structure.collection.ListSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public final class TreeIdTest {
    @Test
    public void TEST_1() {
        TreeId test1 = new TreeId();
        TreeId test2 = test1.createChild("a");
        TreeId test3 = test2.createChild("b");

        Assertions.assertEquals(0, test1.getDepth());
        Assertions.assertNull(test1.getParent());
        Assertions.assertEquals(1, test2.getDepth());
        Assertions.assertEquals(test1, test2.getParent());
        Assertions.assertEquals(1, test2.getDepth());
        Assertions.assertEquals(test2, test3.getParent());
        Assertions.assertEquals(2, test3.getDepth());
    }

    @Test
    public void TEST_2() {
        TreeId test1 = new TreeId();
        TreeId test2 = test1.createChild("a");
        TreeId test3 = test2.createChild("b");

        Assertions.assertFalse(test1.isChildOf(test1));
        Assertions.assertFalse(test1.isChildOf(test2));
        Assertions.assertFalse(test1.isChildOf(test3));
        Assertions.assertTrue(test2.isChildOf(test1));
        Assertions.assertFalse(test2.isChildOf(test2));
        Assertions.assertFalse(test2.isChildOf(test3));
        Assertions.assertFalse(test3.isChildOf(test1));
        Assertions.assertTrue(test3.isChildOf(test2));
        Assertions.assertFalse(test3.isChildOf(test3));
    }

    @Test
    public void TEST_3() {
        TreeId test1 = new TreeId();
        TreeId test2 = test1.createChild("a");
        TreeId test3 = test2.createChild("b");

        Assertions.assertEquals(List.of(), ListSupport.copyOf(test1.tokenizeFrom(test1)));
        Assertions.assertEquals(List.of(test2), ListSupport.copyOf(test2.tokenizeFrom(test1)));
        Assertions.assertEquals(List.of(test2, test3), ListSupport.copyOf(test3.tokenizeFrom(test1)));
    }

    @Test
    public void TEST_4() {
        TreeId test1 = new TreeId();
        TreeId test2 = test1.createChild("a");
        TreeId test3 = test2.createChild("b");

        Assertions.assertNull(test1.tokenizeFrom(test2));
        Assertions.assertEquals(List.of(), ListSupport.copyOf(test2.tokenizeFrom(test2)));
        Assertions.assertEquals(List.of(test3), ListSupport.copyOf(test3.tokenizeFrom(test2)));
    }

    @Test
    public void TEST_5() {
        TreeId test1 = new TreeId();
        TreeId test2 = test1.createChild("a");
        TreeId test3 = test2.createChild("b");

        Assertions.assertNull(test1.tokenizeFrom(test3));
        Assertions.assertNull(test2.tokenizeFrom(test3));
        Assertions.assertEquals(List.of(), ListSupport.copyOf(test3.tokenizeFrom(test3)));
    }

    @Test
    public void TEST_6() {
        TreeId test1 = new TreeId();
        TreeId test2 = test1.createChild("a");
        TreeId test3 = test2.createChild("b");
        TreeId test4 = new TreeId();

        Assertions.assertEquals(List.of(), ListSupport.copyOf(test1.tokenizeFrom(test4)));
        Assertions.assertEquals(List.of(test2), ListSupport.copyOf(test2.tokenizeFrom(test4)));
        Assertions.assertEquals(List.of(test2, test3), ListSupport.copyOf(test3.tokenizeFrom(test4)));
    }
}
