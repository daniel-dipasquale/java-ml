package com.dipasquale.data.structure.deque;

import com.dipasquale.common.test.ThrowableComparer;
import com.dipasquale.data.structure.collection.test.CollectionAsserter;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.NoSuchElementException;

public final class NodeDequeDefaultTest {
    private static final NodeDeque<String> TEST = new NodeDequeDefault<>();

    @Before
    public void before() {
        TEST.clear();
    }

    private static void assertEmptyState() {
        Assert.assertEquals(0, TEST.size());
        Assert.assertTrue(TEST.isEmpty());
        Assert.assertNull(TEST.first());
        Assert.assertNull(TEST.peek());

        try {
            TEST.element();
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(NoSuchElementException.class)
                    .build(), ThrowableComparer.create(e));
        }

        Assert.assertNull(TEST.last());
    }

    @Test
    public void TEST_1() {
        assertEmptyState();
    }

    private static void assertEmptyState(final Node node, final String value) {
        assertEmptyState();
        Assert.assertFalse(TEST.contains(value));
        Assert.assertFalse(TEST.contains(node));
        Assert.assertNull(TEST.peekPrevious(node));
        Assert.assertNull(TEST.peekNext(node));
        Assert.assertEquals(value, TEST.getValue(node));
    }

    @Test
    public void TEST_2() {
        Node node = TEST.createUnbound("item-1");

        Assert.assertNotNull(node);
        assertEmptyState(node, "item-1");
    }

    @Test
    public void TEST_3() {
        Node node = TEST.createUnbound("item-1");

        Assert.assertTrue(TEST.add(node));
        Assert.assertEquals(1, TEST.size());
        Assert.assertFalse(TEST.isEmpty());
        Assert.assertFalse(TEST.contains("item-1"));
        Assert.assertTrue(TEST.contains(node));
        Assert.assertEquals(node, TEST.first());
        Assert.assertEquals(node, TEST.peek());
        Assert.assertEquals(node, TEST.element());
        Assert.assertEquals(node, TEST.last());
        Assert.assertNull(TEST.peekPrevious(node));
        Assert.assertNull(TEST.peekNext(node));
        Assert.assertEquals("item-1", TEST.getValue(node));
    }

    @Test
    public void TEST_4() {
        Node node = TEST.createUnbound("item-1");

        Assert.assertTrue(TEST.add(node));

        try {
            TEST.add(node);
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("node was already added")
                    .build(), ThrowableComparer.create(e));
        }

        Assert.assertFalse(TEST.offer(node));
    }

    @Test
    public void TEST_5() {
        Node node = TEST.createUnbound("item-1");

        Assert.assertTrue(TEST.offer(node));
        Assert.assertFalse(TEST.offer(node));

        try {
            TEST.add(node);
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("node was already added")
                    .build(), ThrowableComparer.create(e));
        }
    }

    @Test
    public void TEST_6() {
        Node node = TEST.createUnbound("item-1");

        Assert.assertFalse(TEST.remove(node));
        Assert.assertTrue(TEST.add(node));
        Assert.assertFalse(TEST.remove("item-1"));
        Assert.assertTrue(TEST.remove(node));
        assertEmptyState(node, "item-1");
    }

    @Test
    public void TEST_7() {
        Node node = TEST.createUnbound("item-1");

        Assert.assertNull(TEST.poll());
        Assert.assertTrue(TEST.add(node));
        Assert.assertEquals(node, TEST.poll());
        assertEmptyState(node, "item-1");
    }

    @Test
    public void TEST_8() {
        Node node1 = TEST.createUnbound("item-1");
        Node node2 = TEST.createUnbound("item-2");
        Node node3 = TEST.createUnbound("item-3");

        Assert.assertTrue(TEST.add(node1));
        Assert.assertTrue(TEST.add(node2));
        Assert.assertTrue(TEST.add(node3));
        Assert.assertEquals(node1, TEST.first());
        Assert.assertEquals(node2, TEST.peekNext(node1));
        Assert.assertEquals(node2, TEST.peekPrevious(node3));
        Assert.assertEquals(node3, TEST.last());
        Assert.assertTrue(TEST.offerLast(node2));
        Assert.assertEquals(node1, TEST.first());
        Assert.assertEquals(node3, TEST.peekNext(node1));
        Assert.assertEquals(node3, TEST.peekPrevious(node2));
        Assert.assertEquals(node2, TEST.last());
    }

    @Test
    public void TEST_9() {
        Node node = TEST.createUnbound("item-1");

        Assert.assertTrue(TEST.offerLast(node));
    }

    @Test
    public void TEST_10() {
        Node node1 = TEST.createUnbound("item-1");
        Node node2 = TEST.createUnbound("item-2");

        Assert.assertTrue(TEST.add(node1));
        Assert.assertTrue(TEST.add(node2));
        Assert.assertEquals(2, TEST.size());
        Assert.assertEquals(node1, TEST.first());
        Assert.assertEquals(node1, TEST.peek());
        Assert.assertEquals(node1, TEST.element());
        Assert.assertEquals(node2, TEST.last());
        Assert.assertEquals(node1, TEST.peekPrevious(node2));
        Assert.assertEquals(node2, TEST.peekNext(node1));
    }

    @Test
    public void TEST_11() {
        Node node1 = TEST.createUnbound("item-1");
        Node node2 = TEST.createUnbound("item-2");

        Assert.assertTrue(TEST.add(node1));
        Assert.assertTrue(TEST.add(node2));
        Assert.assertTrue(TEST.remove(node1));
        Assert.assertEquals(node2, TEST.first());
        Assert.assertEquals(node2, TEST.peek());
        Assert.assertEquals(node2, TEST.element());
        Assert.assertEquals(node2, TEST.last());
        Assert.assertNull(TEST.peekPrevious(node2));
        Assert.assertNull(TEST.peekNext(node2));
    }

    @Test
    public void TEST_12() {
        Node node1 = TEST.createUnbound("item-1");
        Node node2 = TEST.createUnbound("item-2");

        Assert.assertTrue(TEST.add(node1));
        Assert.assertTrue(TEST.add(node2));
        Assert.assertTrue(TEST.remove(node2));
        Assert.assertEquals(node1, TEST.first());
        Assert.assertEquals(node1, TEST.peek());
        Assert.assertEquals(node1, TEST.element());
        Assert.assertEquals(node1, TEST.last());
        Assert.assertNull(TEST.peekPrevious(node1));
        Assert.assertNull(TEST.peekNext(node1));
    }

    private static void assertClearedState(final Node node) {
        assertEmptyState();
        Assert.assertFalse(TEST.contains(node));
        Assert.assertNull(TEST.peekPrevious(node));
        Assert.assertNull(TEST.peekNext(node));
        Assert.assertNull(TEST.getValue(node));
    }

    @Test
    public void TEST_13() {
        Node node1 = TEST.createUnbound("item-1");
        Node node2 = TEST.createUnbound("item-2");

        Assert.assertTrue(TEST.add(node1));
        Assert.assertTrue(TEST.add(node2));
        TEST.clear();
        assertClearedState(node1);
        assertClearedState(node2);
    }

    @Test
    public void TEST_14() {
        Node node = TEST.createUnbound("item-1");

        Assert.assertTrue(TEST.add(node));
        TEST.clear();

        try {
            TEST.add(node);
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("node was not created by this queue")
                    .build(), ThrowableComparer.create(e));
        }

        Assert.assertFalse(TEST.offer(node));
    }

    @Test
    public void TEST_15() {
        Node node = TEST.createUnbound("item-1");

        Assert.assertTrue(TEST.add(node));
        TEST.clear();
        Assert.assertFalse(TEST.remove(node));
        Assert.assertFalse(TEST.offerLast(node));
    }

    @Test
    public void TEST_16() {
        Node node1 = TEST.createUnbound("item-1");
        Node node2 = TEST.createUnbound("item-2");
        Node node3 = TEST.createUnbound("item-3");

        Assert.assertTrue(TEST.add(node1));
        Assert.assertTrue(TEST.add(node2));
        Assert.assertTrue(TEST.add(node3));

        Assert.assertEquals(ImmutableList.builder()
                .add(node1)
                .add(node2)
                .add(node3)
                .build(), ImmutableList.copyOf(TEST));
    }

    @Test
    public void TEST_17() {
        Node node1 = TEST.createUnbound("item-1");
        Node node2 = TEST.createUnbound("item-2");
        Node node3 = TEST.createUnbound("item-3");

        Assert.assertTrue(TEST.add(node1));
        Assert.assertTrue(TEST.add(node2));
        Assert.assertTrue(TEST.add(node3));

        Assert.assertEquals(ImmutableList.builder()
                .add(node3)
                .add(node2)
                .add(node1)
                .build(), ImmutableList.copyOf(TEST::iteratorDescending));
    }

    @Test
    public void TEST_18() {
        CollectionAsserter<Node> collectionAsserter = new CollectionAsserter<>(TEST, i -> TEST.createUnbound(String.format("item-%d", i)), Node.class, Assert::assertEquals);

        collectionAsserter.assertToArray();
        collectionAsserter.assertContainsAll();
        collectionAsserter.assertAddAll();
        collectionAsserter.assertRetainAll();
        collectionAsserter.assertRemoveAll();
    }
}
