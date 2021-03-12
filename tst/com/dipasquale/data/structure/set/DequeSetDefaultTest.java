package com.dipasquale.data.structure.set;

import com.dipasquale.data.structure.collection.test.CollectionAsserter;
import com.dipasquale.data.structure.deque.Node;
import com.dipasquale.data.structure.deque.NodeDeque;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public final class DequeSetDefaultTest { // TODO: redo these test cases
    private static final Map<String, Node> NODES_MAP = new HashMap<>();
    private static final NodeDeque<String> NODES_QUEUE = NodeDeque.create();
    private static final DequeSetDefault<String> TEST = new DequeSetDefault<>(NODES_MAP, NODES_QUEUE);

    @Before
    public void before() {
        TEST.clear();
    }

    @Test
    public void TEST_1() {
        Assert.assertEquals(0, TEST.size());
        Assert.assertTrue(TEST.isEmpty());
        Assert.assertFalse(TEST.contains("item-1"));
        Assert.assertNull(TEST.getFirst());
        Assert.assertNull(TEST.getLast());
    }

    @Test
    public void TEST_2() {
        Assert.assertTrue(TEST.add("item-1"));
        Assert.assertEquals(1, TEST.size());
        Assert.assertFalse(TEST.isEmpty());
        Assert.assertTrue(TEST.contains("item-1"));
        Assert.assertEquals("item-1", TEST.getFirst());
        Assert.assertEquals("item-1", TEST.getLast());
        Assert.assertFalse(TEST.add("item-1"));
        Assert.assertEquals(1, TEST.size());
        Assert.assertFalse(TEST.isEmpty());
        Assert.assertTrue(TEST.contains("item-1"));
        Assert.assertEquals("item-1", TEST.getFirst());
        Assert.assertEquals("item-1", TEST.getLast());
    }

    @Test
    public void TEST_3() {
        Assert.assertTrue(TEST.addLast("item-1"));
        Assert.assertFalse(TEST.add("item-1"));
    }

    @Test
    public void TEST_4() {
        Assert.assertNull(TEST.getFirst());
        Assert.assertTrue(TEST.add("item-1"));
        Assert.assertEquals("item-1", TEST.getFirst());
    }

    @Test
    public void TEST_5() {
        Assert.assertFalse(TEST.remove("item-1"));
        Assert.assertTrue(TEST.add("item-1"));
        Assert.assertTrue(TEST.remove("item-1"));
        Assert.assertTrue(TEST.add("item-1"));
    }

    @Test
    public void TEST_6() {
        Assert.assertNull(TEST.removeFirst());
        Assert.assertTrue(TEST.add("item-1"));
        Assert.assertEquals("item-1", TEST.removeFirst());
        Assert.assertTrue(TEST.add("item-1"));
        Assert.assertEquals("item-1", TEST.removeLast());
        Assert.assertTrue(TEST.add("item-1"));
    }

    @Test
    public void TEST_7() {
        TEST.clear();
        Assert.assertTrue(TEST.add("item-1"));
        Assert.assertTrue(TEST.add("item-2"));
        Assert.assertTrue(TEST.add("item-3"));
        Assert.assertEquals(3, TEST.size());
        TEST.clear();
        Assert.assertEquals(0, TEST.size());
    }

    @Test
    public void TEST_8() {
        Assert.assertTrue(TEST.add("item-1"));
        Assert.assertTrue(TEST.add("item-2"));
        Assert.assertTrue(TEST.add("item-3"));

        Assert.assertEquals(ImmutableList.builder()
                .add("item-1")
                .add("item-2")
                .add("item-3")
                .build(), ImmutableList.copyOf(TEST));
    }

    @Test
    public void TEST_9() {
        Assert.assertTrue(TEST.add("item-1"));
        Assert.assertTrue(TEST.add("item-2"));
        Assert.assertTrue(TEST.add("item-3"));
        Assert.assertTrue(TEST.remove("item-2"));
        Assert.assertTrue(TEST.add("item-2"));

        Assert.assertEquals(ImmutableList.builder()
                .add("item-1")
                .add("item-3")
                .add("item-2")
                .build(), ImmutableList.copyOf(TEST));
    }

    @Test
    public void TEST_10() {
        Assert.assertTrue(TEST.add("item-1"));
        Assert.assertTrue(TEST.add("item-2"));
        Assert.assertTrue(TEST.add("item-3"));

        Assert.assertEquals(ImmutableList.builder()
                .add("item-3")
                .add("item-2")
                .add("item-1")
                .build(), ImmutableList.copyOf(TEST::descendingIterator));
    }

    @Test
    public void TEST_11() {
        Assert.assertTrue(TEST.add("item-1"));
        Assert.assertTrue(TEST.add("item-2"));
        Assert.assertTrue(TEST.add("item-3"));
        Assert.assertTrue(TEST.remove("item-2"));
        Assert.assertTrue(TEST.add("item-2"));

        Assert.assertEquals(ImmutableList.builder()
                .add("item-2")
                .add("item-3")
                .add("item-1")
                .build(), ImmutableList.copyOf(TEST::descendingIterator));
    }

    @Test
    public void TEST_12() {
        CollectionAsserter<String> collectionAsserter = new CollectionAsserter<>(TEST, i -> String.format("item-%d", i), String.class, Assert::assertEquals);

        collectionAsserter.assertToArray();
        collectionAsserter.assertContainsAll();
        collectionAsserter.assertAddAll();
        collectionAsserter.assertRetainAll();
        collectionAsserter.assertRemoveAll();
    }
}
