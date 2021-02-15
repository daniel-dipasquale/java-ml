package com.dipasquale.data.structure.set;

import com.dipasquale.common.test.ThrowableComparer;
import com.dipasquale.data.structure.collection.test.CollectionAsserter;
import com.dipasquale.data.structure.queue.Node;
import com.dipasquale.data.structure.queue.NodeQueue;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public final class InsertOrderSetDefaultTest {
    private static final Map<String, Node> NODES_MAP = new HashMap<>();
    private static final NodeQueue<String> NODES_QUEUE = NodeQueue.create();
    private static final InsertOrderSetDefault<String> TEST = new InsertOrderSetDefault<>(NODES_MAP, NODES_QUEUE);

    @Before
    public void before() {
        TEST.clear();
    }

    @Test
    public void TEST_1() {
        Assert.assertEquals(0, TEST.size());
        Assert.assertTrue(TEST.isEmpty());
        Assert.assertFalse(TEST.contains("item-1"));
        Assert.assertNull(TEST.first());
        Assert.assertNull(TEST.last());
    }

    @Test
    public void TEST_2() {
        Assert.assertTrue(TEST.add("item-1"));
        Assert.assertEquals(1, TEST.size());
        Assert.assertFalse(TEST.isEmpty());
        Assert.assertTrue(TEST.contains("item-1"));
        Assert.assertEquals("item-1", TEST.first());
        Assert.assertEquals("item-1", TEST.last());
        Assert.assertFalse(TEST.add("item-1"));
        Assert.assertEquals(1, TEST.size());
        Assert.assertFalse(TEST.isEmpty());
        Assert.assertTrue(TEST.contains("item-1"));
        Assert.assertEquals("item-1", TEST.first());
        Assert.assertEquals("item-1", TEST.last());
    }

    @Test
    public void TEST_3() {
        Assert.assertTrue(TEST.offer("item-1"));
        Assert.assertFalse(TEST.add("item-1"));
    }

    @Test
    public void TEST_4() {
        try {
            TEST.element();
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(NoSuchElementException.class)
                    .build(), ThrowableComparer.create(e));
        }

        Assert.assertNull(TEST.peek());
        Assert.assertTrue(TEST.add("item-1"));
        Assert.assertEquals("item-1", TEST.first());
        Assert.assertEquals("item-1", TEST.element());
        Assert.assertEquals("item-1", TEST.peek());
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
        try {
            TEST.remove();
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(NoSuchElementException.class)
                    .build(), ThrowableComparer.create(e));
        }

        Assert.assertNull(TEST.poll());
        Assert.assertTrue(TEST.add("item-1"));
        Assert.assertEquals("item-1", TEST.remove());
        Assert.assertTrue(TEST.add("item-1"));
        Assert.assertEquals("item-1", TEST.poll());
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
                .build(), ImmutableList.copyOf(TEST::iteratorDescending));
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
                .build(), ImmutableList.copyOf(TEST::iteratorDescending));
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
