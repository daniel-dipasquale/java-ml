package com.dipasquale.data.structure.queue;

import com.dipasquale.common.test.ThrowableAsserter;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.NoSuchElementException;

public final class NodeQueueDefaultTest {
    private static final NodeQueue<String> TEST = NodeQueue.create();

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
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(NoSuchElementException.class)
                    .build(), ThrowableAsserter.create(e));
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
        Assert.assertNull(TEST.previous(node));
        Assert.assertNull(TEST.next(node));
        Assert.assertEquals(value, TEST.getValue(node));
    }

    @Test
    public void TEST_2() {
        Node node = TEST.createUnlinked("item-1");

        Assert.assertNotNull(node);
        assertEmptyState(node, "item-1");
    }

    @Test
    public void TEST_3() {
        Node node = TEST.createUnlinked("item-1");

        Assert.assertTrue(TEST.add(node));
        Assert.assertEquals(1, TEST.size());
        Assert.assertFalse(TEST.isEmpty());
        Assert.assertFalse(TEST.contains("item-1"));
        Assert.assertTrue(TEST.contains(node));
        Assert.assertEquals(node, TEST.first());
        Assert.assertEquals(node, TEST.peek());
        Assert.assertEquals(node, TEST.element());
        Assert.assertEquals(node, TEST.last());
        Assert.assertNull(TEST.previous(node));
        Assert.assertNull(TEST.next(node));
        Assert.assertEquals("item-1", TEST.getValue(node));

        try {
            TEST.add(node);
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("node was already added")
                    .build(), ThrowableAsserter.create(e));
        }
    }

    @Test
    public void TEST_4() {
        Node node = TEST.createUnlinked("item-1");

        Assert.assertTrue(TEST.add(node));

        try {
            TEST.add(node);
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("node was already added")
                    .build(), ThrowableAsserter.create(e));
        }

        Assert.assertFalse(TEST.offer(node));
    }

    @Test
    public void TEST_5() {
        Node node = TEST.createUnlinked("item-1");

        Assert.assertTrue(TEST.offer(node));
        Assert.assertFalse(TEST.offer(node));

        try {
            TEST.add(node);
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("node was already added")
                    .build(), ThrowableAsserter.create(e));
        }
    }

    @Test
    public void TEST_6() {
        Node node = TEST.createUnlinked("item-1");

        Assert.assertFalse(TEST.remove(node));
        Assert.assertTrue(TEST.add(node));
        Assert.assertFalse(TEST.remove("item-1"));
        Assert.assertTrue(TEST.remove(node));
        assertEmptyState(node, "item-1");
    }

    @Test
    public void TEST_7() {
        Node node = TEST.createUnlinked("item-1");

        Assert.assertNull(TEST.poll());
        Assert.assertTrue(TEST.add(node));
        Assert.assertEquals(node, TEST.poll());
        assertEmptyState(node, "item-1");
    }

    @Test
    public void TEST_8() {
        Node node1 = TEST.createUnlinked("item-1");
        Node node2 = TEST.createUnlinked("item-2");
        Node node3 = TEST.createUnlinked("item-3");

        Assert.assertTrue(TEST.add(node1));
        Assert.assertTrue(TEST.add(node2));
        Assert.assertTrue(TEST.add(node3));
        Assert.assertEquals(node1, TEST.first());
        Assert.assertEquals(node2, TEST.next(node1));
        Assert.assertEquals(node2, TEST.previous(node3));
        Assert.assertEquals(node3, TEST.last());
        Assert.assertTrue(TEST.reoffer(node2));
        Assert.assertEquals(node1, TEST.first());
        Assert.assertEquals(node3, TEST.next(node1));
        Assert.assertEquals(node3, TEST.previous(node2));
        Assert.assertEquals(node2, TEST.last());
    }

    @Test
    public void TEST_9() {
        Node node = TEST.createUnlinked("item-1");

        Assert.assertFalse(TEST.reoffer(node));
    }

    @Test
    public void TEST_10() {
        Node node1 = TEST.createUnlinked("item-1");
        Node node2 = TEST.createUnlinked("item-2");

        Assert.assertTrue(TEST.add(node1));
        Assert.assertTrue(TEST.add(node2));
        Assert.assertEquals(2, TEST.size());
        Assert.assertEquals(node1, TEST.first());
        Assert.assertEquals(node1, TEST.peek());
        Assert.assertEquals(node1, TEST.element());
        Assert.assertEquals(node2, TEST.last());
        Assert.assertEquals(node1, TEST.previous(node2));
        Assert.assertEquals(node2, TEST.next(node1));
    }

    @Test
    public void TEST_11() {
        Node node1 = TEST.createUnlinked("item-1");
        Node node2 = TEST.createUnlinked("item-2");

        Assert.assertTrue(TEST.add(node1));
        Assert.assertTrue(TEST.add(node2));
        Assert.assertTrue(TEST.remove(node1));
        Assert.assertEquals(node2, TEST.first());
        Assert.assertEquals(node2, TEST.peek());
        Assert.assertEquals(node2, TEST.element());
        Assert.assertEquals(node2, TEST.last());
        Assert.assertNull(TEST.previous(node2));
        Assert.assertNull(TEST.next(node2));
    }

    @Test
    public void TEST_12() {
        Node node1 = TEST.createUnlinked("item-1");
        Node node2 = TEST.createUnlinked("item-2");

        Assert.assertTrue(TEST.add(node1));
        Assert.assertTrue(TEST.add(node2));
        Assert.assertTrue(TEST.remove(node2));
        Assert.assertEquals(node1, TEST.first());
        Assert.assertEquals(node1, TEST.peek());
        Assert.assertEquals(node1, TEST.element());
        Assert.assertEquals(node1, TEST.last());
        Assert.assertNull(TEST.previous(node1));
        Assert.assertNull(TEST.next(node1));
    }

    private static void assertClearedState(final Node node) {
        assertEmptyState();
        Assert.assertFalse(TEST.contains(node));
        Assert.assertNull(TEST.previous(node));
        Assert.assertNull(TEST.next(node));
        Assert.assertNull(TEST.getValue(node));
    }

    @Test
    public void TEST_13() {
        Node node1 = TEST.createUnlinked("item-1");
        Node node2 = TEST.createUnlinked("item-2");

        Assert.assertTrue(TEST.add(node1));
        Assert.assertTrue(TEST.add(node2));
        TEST.clear();
        assertClearedState(node1);
        assertClearedState(node2);
    }

    @Test
    public void TEST_14() {
        Node node = TEST.createUnlinked("item-1");

        Assert.assertTrue(TEST.add(node));
        TEST.clear();

        try {
            TEST.add(node);
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("node was not created by this queue")
                    .build(), ThrowableAsserter.create(e));
        }

        Assert.assertFalse(TEST.offer(node));
    }

    @Test
    public void TEST_15() {
        Node node = TEST.createUnlinked("item-1");

        Assert.assertTrue(TEST.add(node));
        TEST.clear();
        Assert.assertFalse(TEST.remove(node));
        Assert.assertFalse(TEST.reoffer(node));
    }

    @Test
    public void TEST_16() {
        Node node1 = TEST.createUnlinked("item-1");
        Node node2 = TEST.createUnlinked("item-2");
        Node node3 = TEST.createUnlinked("item-3");

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
        Node node1 = TEST.createUnlinked("item-1");
        Node node2 = TEST.createUnlinked("item-2");
        Node node3 = TEST.createUnlinked("item-3");

        Assert.assertTrue(TEST.add(node1));
        Assert.assertTrue(TEST.add(node2));
        Assert.assertTrue(TEST.add(node3));

        Assert.assertEquals(ImmutableList.builder()
                .add(node3)
                .add(node2)
                .add(node1)
                .build(), ImmutableList.copyOf(TEST::iteratorDescending));
    }
}
