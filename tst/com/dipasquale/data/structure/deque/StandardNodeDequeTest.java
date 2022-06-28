package com.dipasquale.data.structure.deque;

import com.dipasquale.common.error.ErrorSnapshot;
import com.dipasquale.data.structure.collection.CollectionAsserter;
import com.dipasquale.data.structure.collection.ListSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

public final class StandardNodeDequeTest { // TODO: redo these test cases
    private static final StandardNodeDeque<String> TEST = new StandardNodeDeque<>();

    @BeforeEach
    public void beforeEach() {
        TEST.clear();
    }

    private static void assertEmptyState() {
        Assertions.assertEquals(0, TEST.size());
        Assertions.assertTrue(TEST.isEmpty());
        Assertions.assertNull(TEST.peek());
        Assertions.assertNull(TEST.peekFirst());
        Assertions.assertNull(TEST.peekLast());

        try {
            TEST.getFirst();
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(NoSuchElementException.class)
                    .message("the deque is empty")
                    .build(), ErrorSnapshot.create(e));
        }

        try {
            TEST.getLast();
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(NoSuchElementException.class)
                    .message("the deque is empty")
                    .build(), ErrorSnapshot.create(e));
        }

        try {
            TEST.element();
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(NoSuchElementException.class)
                    .message("the deque is empty")
                    .build(), ErrorSnapshot.create(e));
        }
    }

    @Test
    public void TEST_1() {
        assertEmptyState();
    }

    private static void assertEmptyState(final StandardNode<String> node, final String value) {
        assertEmptyState();
        Assertions.assertFalse(TEST.contains(value));
        Assertions.assertFalse(TEST.contains(node));
        Assertions.assertNull(TEST.peekPrevious(node));
        Assertions.assertNull(TEST.peekNext(node));
        Assertions.assertEquals(value, TEST.getValue(node));
    }

    @Test
    public void TEST_2() {
        StandardNode<String> node = TEST.createUnbound("item-1");

        Assertions.assertNotNull(node);
        assertEmptyState(node, "item-1");
    }

    @Test
    public void TEST_3() {
        StandardNode<String> node = TEST.createUnbound("item-1");

        Assertions.assertTrue(TEST.add(node));
        Assertions.assertEquals(1, TEST.size());
        Assertions.assertFalse(TEST.isEmpty());
        Assertions.assertFalse(TEST.contains("item-1"));
        Assertions.assertTrue(TEST.contains(node));
        Assertions.assertEquals(node, TEST.peekFirst());
        Assertions.assertEquals(node, TEST.peek());
        Assertions.assertEquals(node, TEST.element());
        Assertions.assertEquals(node, TEST.peekLast());
        Assertions.assertEquals(node, TEST.getLast());
        Assertions.assertNull(TEST.peekPrevious(node));
        Assertions.assertNull(TEST.peekNext(node));
        Assertions.assertEquals("item-1", TEST.getValue(node));
    }

    @Test
    public void TEST_4() {
        StandardNode<String> node = TEST.createUnbound("item-1");

        Assertions.assertTrue(TEST.add(node));
        Assertions.assertTrue(TEST.add(node));
        Assertions.assertTrue(TEST.offer(node));
    }

    @Test
    public void TEST_5() {
        StandardNode<String> node = TEST.createUnbound("item-1");

        Assertions.assertTrue(TEST.offer(node));
        Assertions.assertTrue(TEST.offer(node));
        Assertions.assertTrue(TEST.add(node));
    }

    @Test
    public void TEST_6() {
        StandardNode<String> node = TEST.createUnbound("item-1");

        Assertions.assertFalse(TEST.remove(node));
        Assertions.assertTrue(TEST.add(node));
        Assertions.assertFalse(TEST.remove("item-1"));
        Assertions.assertTrue(TEST.remove(node));
        assertEmptyState(node, "item-1");
    }

    @Test
    public void TEST_7() {
        StandardNode<String> node = TEST.createUnbound("item-1");

        Assertions.assertNull(TEST.poll());
        Assertions.assertTrue(TEST.add(node));
        Assertions.assertEquals(node, TEST.poll());
        assertEmptyState(node, "item-1");
    }

    @Test
    public void TEST_8() {
        StandardNode<String> node1 = TEST.createUnbound("item-1");
        StandardNode<String> node2 = TEST.createUnbound("item-2");
        StandardNode<String> node3 = TEST.createUnbound("item-3");

        Assertions.assertTrue(TEST.add(node1));
        Assertions.assertTrue(TEST.add(node2));
        Assertions.assertTrue(TEST.add(node3));
        Assertions.assertEquals(node1, TEST.peekFirst());
        Assertions.assertEquals(node1, TEST.getFirst());
        Assertions.assertEquals(node2, TEST.peekNext(node1));
        Assertions.assertEquals(node2, TEST.peekPrevious(node3));
        Assertions.assertEquals(node3, TEST.peekLast());
        Assertions.assertEquals(node3, TEST.getLast());
        Assertions.assertTrue(TEST.offerLast(node2));
        Assertions.assertEquals(node1, TEST.peekFirst());
        Assertions.assertEquals(node1, TEST.getFirst());
        Assertions.assertEquals(node3, TEST.peekNext(node1));
        Assertions.assertEquals(node3, TEST.peekPrevious(node2));
        Assertions.assertEquals(node2, TEST.peekLast());
        Assertions.assertEquals(node2, TEST.getLast());
    }

    @Test
    public void TEST_9() {
        StandardNode<String> node = TEST.createUnbound("item-1");

        Assertions.assertTrue(TEST.offerLast(node));
    }

    @Test
    public void TEST_10() {
        StandardNode<String> node1 = TEST.createUnbound("item-1");
        StandardNode<String> node2 = TEST.createUnbound("item-2");

        Assertions.assertTrue(TEST.add(node1));
        Assertions.assertTrue(TEST.add(node2));
        Assertions.assertEquals(2, TEST.size());
        Assertions.assertEquals(node1, TEST.peek());
        Assertions.assertEquals(node1, TEST.peekFirst());
        Assertions.assertEquals(node1, TEST.getFirst());
        Assertions.assertEquals(node1, TEST.element());
        Assertions.assertEquals(node2, TEST.peekLast());
        Assertions.assertEquals(node2, TEST.getLast());
        Assertions.assertEquals(node1, TEST.peekPrevious(node2));
        Assertions.assertEquals(node2, TEST.peekNext(node1));
    }

    @Test
    public void TEST_11() {
        StandardNode<String> node1 = TEST.createUnbound("item-1");
        StandardNode<String> node2 = TEST.createUnbound("item-2");

        Assertions.assertTrue(TEST.add(node1));
        Assertions.assertTrue(TEST.add(node2));
        Assertions.assertTrue(TEST.remove(node1));
        Assertions.assertEquals(node2, TEST.peek());
        Assertions.assertEquals(node2, TEST.peekFirst());
        Assertions.assertEquals(node2, TEST.getFirst());
        Assertions.assertEquals(node2, TEST.element());
        Assertions.assertEquals(node2, TEST.peekLast());
        Assertions.assertEquals(node2, TEST.getLast());
        Assertions.assertNull(TEST.peekPrevious(node2));
        Assertions.assertNull(TEST.peekNext(node2));
    }

    @Test
    public void TEST_12() {
        StandardNode<String> node1 = TEST.createUnbound("item-1");
        StandardNode<String> node2 = TEST.createUnbound("item-2");

        Assertions.assertTrue(TEST.add(node1));
        Assertions.assertTrue(TEST.add(node2));
        Assertions.assertTrue(TEST.remove(node2));
        Assertions.assertEquals(node1, TEST.peek());
        Assertions.assertEquals(node1, TEST.peekFirst());
        Assertions.assertEquals(node1, TEST.getFirst());
        Assertions.assertEquals(node1, TEST.element());
        Assertions.assertEquals(node1, TEST.peekLast());
        Assertions.assertEquals(node1, TEST.getLast());
        Assertions.assertNull(TEST.peekPrevious(node1));
        Assertions.assertNull(TEST.peekNext(node1));
    }

    private static void assertClearedState(final StandardNode<String> node) {
        assertEmptyState();
        Assertions.assertFalse(TEST.contains(node));
        Assertions.assertNull(TEST.peekPrevious(node));
        Assertions.assertNull(TEST.peekNext(node));
        Assertions.assertNull(TEST.getValue(node));
    }

    @Test
    public void TEST_13() {
        StandardNode<String> node1 = TEST.createUnbound("item-1");
        StandardNode<String> node2 = TEST.createUnbound("item-2");

        Assertions.assertTrue(TEST.add(node1));
        Assertions.assertTrue(TEST.add(node2));
        TEST.clear();
        assertClearedState(node1);
        assertClearedState(node2);
    }

    @Test
    public void TEST_14() {
        StandardNode<String> node = TEST.createUnbound("item-1");

        Assertions.assertTrue(TEST.add(node));
        TEST.clear();

        try {
            TEST.add(node);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("node was not created by this deque")
                    .build(), ErrorSnapshot.create(e));
        }

        Assertions.assertFalse(TEST.offer(node));
    }

    @Test
    public void TEST_15() {
        StandardNode<String> node = TEST.createUnbound("item-1");

        Assertions.assertTrue(TEST.add(node));
        TEST.clear();
        Assertions.assertFalse(TEST.remove(node));
        Assertions.assertFalse(TEST.offerLast(node));
    }

    @Test
    public void TEST_16() {
        StandardNode<String> node1 = TEST.createUnbound("item-1");
        StandardNode<String> node2 = TEST.createUnbound("item-2");
        StandardNode<String> node3 = TEST.createUnbound("item-3");

        Assertions.assertTrue(TEST.add(node1));
        Assertions.assertTrue(TEST.add(node2));
        Assertions.assertTrue(TEST.add(node3));
        Assertions.assertEquals(List.of(node1, node2, node3), List.copyOf(TEST));
    }

    @Test
    public void TEST_17() {
        StandardNode<String> node1 = TEST.createUnbound("item-1");
        StandardNode<String> node2 = TEST.createUnbound("item-2");
        StandardNode<String> node3 = TEST.createUnbound("item-3");

        Assertions.assertTrue(TEST.add(node1));
        Assertions.assertTrue(TEST.add(node2));
        Assertions.assertTrue(TEST.add(node3));
        Assertions.assertEquals(List.of(node3, node2, node1), ListSupport.copyOf(TEST.descendingIterator()));
    }

    @Test
    public void TEST_18() {
        CollectionAsserter<StandardNode<String>> collectionAsserter = new CollectionAsserter<>(TEST, index -> TEST.createUnbound(String.format("item-%d", index)), StandardNode.class, Assertions::assertEquals);

        collectionAsserter.assertToArray();
        collectionAsserter.assertContainsAll();
        collectionAsserter.assertAddAll();
        collectionAsserter.assertRetainAll();
        collectionAsserter.assertRemoveAll();
    }
}
