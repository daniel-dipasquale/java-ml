package com.dipasquale.search.mcts;

import com.dipasquale.data.structure.collection.ListSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public final class StateIdTest {
    @Test
    public void TEST_1() {
        StateId test1 = new StateId();
        StateId test2 = test1.createChild(1);
        StateId test3 = test2.createChild(2);

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
        StateId test1 = new StateId();
        StateId test2 = test1.createChild(1);
        StateId test3 = test2.createChild(2);

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
        StateId test1 = new StateId();
        StateId test2 = test1.createChild(1);
        StateId test3 = test2.createChild(2);

        Assertions.assertEquals(List.of(), ListSupport.copyOf(test1.tokenizeUntil(test1)));
        Assertions.assertEquals(List.of(test2), ListSupport.copyOf(test2.tokenizeUntil(test1)));
        Assertions.assertEquals(List.of(test2, test3), ListSupport.copyOf(test3.tokenizeUntil(test1)));
    }

    @Test
    public void TEST_4() {
        StateId test1 = new StateId();
        StateId test2 = test1.createChild(1);
        StateId test3 = test2.createChild(2);

        Assertions.assertNull(test1.tokenizeUntil(test2));
        Assertions.assertEquals(List.of(), ListSupport.copyOf(test2.tokenizeUntil(test2)));
        Assertions.assertEquals(List.of(test3), ListSupport.copyOf(test3.tokenizeUntil(test2)));
    }

    @Test
    public void TEST_5() {
        StateId test1 = new StateId();
        StateId test2 = test1.createChild(1);
        StateId test3 = test2.createChild(2);

        Assertions.assertNull(test1.tokenizeUntil(test3));
        Assertions.assertNull(test2.tokenizeUntil(test3));
        Assertions.assertEquals(List.of(), ListSupport.copyOf(test3.tokenizeUntil(test3)));
    }

    @Test
    public void TEST_6() {
        StateId test1 = new StateId();
        StateId test2 = test1.createChild(1);
        StateId test3 = test2.createChild(2);
        StateId test4 = new StateId();

        Assertions.assertEquals(List.of(), ListSupport.copyOf(test1.tokenizeUntil(test4)));
        Assertions.assertEquals(List.of(test2), ListSupport.copyOf(test2.tokenizeUntil(test4)));
        Assertions.assertEquals(List.of(test2, test3), ListSupport.copyOf(test3.tokenizeUntil(test4)));
    }
}
