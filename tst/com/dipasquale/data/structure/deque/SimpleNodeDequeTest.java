package com.dipasquale.data.structure.deque;

import com.dipasquale.common.test.ThrowableComparer;
import com.dipasquale.data.structure.collection.test.CollectionAsserter;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.NoSuchElementException;

public final class SimpleNodeDequeTest { // TODO: redo these test cases
    private static final SimpleNodeDeque<String> TEST = new SimpleNodeDeque<>();

    @Before
    public void before() {
        TEST.clear();
    }

    private static void assertEmptyState() {
        Assert.assertEquals(0, TEST.size());
        Assert.assertTrue(TEST.isEmpty());
        Assert.assertNull(TEST.peek());
        Assert.assertNull(TEST.peekFirst());
        Assert.assertNull(TEST.peekLast());

        try {
            TEST.getFirst();
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(NoSuchElementException.class)
                    .message("the deque is empty")
                    .build(), ThrowableComparer.create(e));
        }

        try {
            TEST.getLast();
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(NoSuchElementException.class)
                    .message("the deque is empty")
                    .build(), ThrowableComparer.create(e));
        }

        try {
            TEST.element();
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(NoSuchElementException.class)
                    .message("the deque is empty")
                    .build(), ThrowableComparer.create(e));
        }
    }

    @Test
    public void TEST_1() {
        assertEmptyState();
    }

    private static void assertEmptyState(final SimpleNode<String> node, final String value) {
        assertEmptyState();
        Assert.assertFalse(TEST.contains(value));
        Assert.assertFalse(TEST.contains(node));
        Assert.assertNull(TEST.peekPrevious(node));
        Assert.assertNull(TEST.peekNext(node));
        Assert.assertEquals(value, TEST.getValue(node));
    }

    @Test
    public void TEST_2() {
        SimpleNode<String> node = TEST.createUnbound("item-1");

        Assert.assertNotNull(node);
        assertEmptyState(node, "item-1");
    }

    @Test
    public void TEST_3() {
        SimpleNode<String> node = TEST.createUnbound("item-1");

        Assert.assertTrue(TEST.add(node));
        Assert.assertEquals(1, TEST.size());
        Assert.assertFalse(TEST.isEmpty());
        Assert.assertFalse(TEST.contains("item-1"));
        Assert.assertTrue(TEST.contains(node));
        Assert.assertEquals(node, TEST.peekFirst());
        Assert.assertEquals(node, TEST.peek());
        Assert.assertEquals(node, TEST.element());
        Assert.assertEquals(node, TEST.peekLast());
        Assert.assertEquals(node, TEST.getLast());
        Assert.assertNull(TEST.peekPrevious(node));
        Assert.assertNull(TEST.peekNext(node));
        Assert.assertEquals("item-1", TEST.getValue(node));
    }

    @Test
    public void TEST_4() {
        SimpleNode<String> node = TEST.createUnbound("item-1");

        Assert.assertTrue(TEST.add(node));
        Assert.assertTrue(TEST.add(node));
        Assert.assertTrue(TEST.offer(node));
    }

    @Test
    public void TEST_5() {
        SimpleNode<String> node = TEST.createUnbound("item-1");

        Assert.assertTrue(TEST.offer(node));
        Assert.assertTrue(TEST.offer(node));
        Assert.assertTrue(TEST.add(node));
    }

    @Test
    public void TEST_6() {
        SimpleNode<String> node = TEST.createUnbound("item-1");

        Assert.assertFalse(TEST.remove(node));
        Assert.assertTrue(TEST.add(node));
        Assert.assertFalse(TEST.remove("item-1"));
        Assert.assertTrue(TEST.remove(node));
        assertEmptyState(node, "item-1");
    }

    @Test
    public void TEST_7() {
        SimpleNode<String> node = TEST.createUnbound("item-1");

        Assert.assertNull(TEST.poll());
        Assert.assertTrue(TEST.add(node));
        Assert.assertEquals(node, TEST.poll());
        assertEmptyState(node, "item-1");
    }

    @Test
    public void TEST_8() {
        SimpleNode<String> node1 = TEST.createUnbound("item-1");
        SimpleNode<String> node2 = TEST.createUnbound("item-2");
        SimpleNode<String> node3 = TEST.createUnbound("item-3");

        Assert.assertTrue(TEST.add(node1));
        Assert.assertTrue(TEST.add(node2));
        Assert.assertTrue(TEST.add(node3));
        Assert.assertEquals(node1, TEST.peekFirst());
        Assert.assertEquals(node1, TEST.getFirst());
        Assert.assertEquals(node2, TEST.peekNext(node1));
        Assert.assertEquals(node2, TEST.peekPrevious(node3));
        Assert.assertEquals(node3, TEST.peekLast());
        Assert.assertEquals(node3, TEST.getLast());
        Assert.assertTrue(TEST.offerLast(node2));
        Assert.assertEquals(node1, TEST.peekFirst());
        Assert.assertEquals(node1, TEST.getFirst());
        Assert.assertEquals(node3, TEST.peekNext(node1));
        Assert.assertEquals(node3, TEST.peekPrevious(node2));
        Assert.assertEquals(node2, TEST.peekLast());
        Assert.assertEquals(node2, TEST.getLast());
    }

    @Test
    public void TEST_9() {
        SimpleNode<String> node = TEST.createUnbound("item-1");

        Assert.assertTrue(TEST.offerLast(node));
    }

    @Test
    public void TEST_10() {
        SimpleNode<String> node1 = TEST.createUnbound("item-1");
        SimpleNode<String> node2 = TEST.createUnbound("item-2");

        Assert.assertTrue(TEST.add(node1));
        Assert.assertTrue(TEST.add(node2));
        Assert.assertEquals(2, TEST.size());
        Assert.assertEquals(node1, TEST.peek());
        Assert.assertEquals(node1, TEST.peekFirst());
        Assert.assertEquals(node1, TEST.getFirst());
        Assert.assertEquals(node1, TEST.element());
        Assert.assertEquals(node2, TEST.peekLast());
        Assert.assertEquals(node2, TEST.getLast());
        Assert.assertEquals(node1, TEST.peekPrevious(node2));
        Assert.assertEquals(node2, TEST.peekNext(node1));
    }

    @Test
    public void TEST_11() {
        SimpleNode<String> node1 = TEST.createUnbound("item-1");
        SimpleNode<String> node2 = TEST.createUnbound("item-2");

        Assert.assertTrue(TEST.add(node1));
        Assert.assertTrue(TEST.add(node2));
        Assert.assertTrue(TEST.remove(node1));
        Assert.assertEquals(node2, TEST.peek());
        Assert.assertEquals(node2, TEST.peekFirst());
        Assert.assertEquals(node2, TEST.getFirst());
        Assert.assertEquals(node2, TEST.element());
        Assert.assertEquals(node2, TEST.peekLast());
        Assert.assertEquals(node2, TEST.getLast());
        Assert.assertNull(TEST.peekPrevious(node2));
        Assert.assertNull(TEST.peekNext(node2));
    }

    @Test
    public void TEST_12() {
        SimpleNode<String> node1 = TEST.createUnbound("item-1");
        SimpleNode<String> node2 = TEST.createUnbound("item-2");

        Assert.assertTrue(TEST.add(node1));
        Assert.assertTrue(TEST.add(node2));
        Assert.assertTrue(TEST.remove(node2));
        Assert.assertEquals(node1, TEST.peek());
        Assert.assertEquals(node1, TEST.peekFirst());
        Assert.assertEquals(node1, TEST.getFirst());
        Assert.assertEquals(node1, TEST.element());
        Assert.assertEquals(node1, TEST.peekLast());
        Assert.assertEquals(node1, TEST.getLast());
        Assert.assertNull(TEST.peekPrevious(node1));
        Assert.assertNull(TEST.peekNext(node1));
    }

    private static void assertClearedState(final SimpleNode<String> node) {
        assertEmptyState();
        Assert.assertFalse(TEST.contains(node));
        Assert.assertNull(TEST.peekPrevious(node));
        Assert.assertNull(TEST.peekNext(node));
        Assert.assertNull(TEST.getValue(node));
    }

    @Test
    public void TEST_13() {
        SimpleNode<String> node1 = TEST.createUnbound("item-1");
        SimpleNode<String> node2 = TEST.createUnbound("item-2");

        Assert.assertTrue(TEST.add(node1));
        Assert.assertTrue(TEST.add(node2));
        TEST.clear();
        assertClearedState(node1);
        assertClearedState(node2);
    }

    @Test
    public void TEST_14() {
        SimpleNode<String> node = TEST.createUnbound("item-1");

        Assert.assertTrue(TEST.add(node));
        TEST.clear();

        try {
            TEST.add(node);
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("node was not created by this deque")
                    .build(), ThrowableComparer.create(e));
        }

        Assert.assertFalse(TEST.offer(node));
    }

    @Test
    public void TEST_15() {
        SimpleNode<String> node = TEST.createUnbound("item-1");

        Assert.assertTrue(TEST.add(node));
        TEST.clear();
        Assert.assertFalse(TEST.remove(node));
        Assert.assertFalse(TEST.offerLast(node));
    }

    @Test
    public void TEST_16() {
        SimpleNode<String> node1 = TEST.createUnbound("item-1");
        SimpleNode<String> node2 = TEST.createUnbound("item-2");
        SimpleNode<String> node3 = TEST.createUnbound("item-3");

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
        SimpleNode<String> node1 = TEST.createUnbound("item-1");
        SimpleNode<String> node2 = TEST.createUnbound("item-2");
        SimpleNode<String> node3 = TEST.createUnbound("item-3");

        Assert.assertTrue(TEST.add(node1));
        Assert.assertTrue(TEST.add(node2));
        Assert.assertTrue(TEST.add(node3));

        Assert.assertEquals(ImmutableList.builder()
                .add(node3)
                .add(node2)
                .add(node1)
                .build(), ImmutableList.copyOf(TEST::descendingIterator));
    }

    @Test
    public void TEST_18() {
        CollectionAsserter<SimpleNode<String>> collectionAsserter = new CollectionAsserter<>(TEST, i -> TEST.createUnbound(String.format("item-%d", i)), SimpleNode.class, Assert::assertEquals);

        collectionAsserter.assertToArray();
        collectionAsserter.assertContainsAll();
        collectionAsserter.assertAddAll();
        collectionAsserter.assertRetainAll();
        collectionAsserter.assertRemoveAll();
    }
}
