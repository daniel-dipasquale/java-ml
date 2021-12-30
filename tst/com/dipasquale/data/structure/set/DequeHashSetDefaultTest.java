package com.dipasquale.data.structure.set;

import com.dipasquale.data.structure.collection.CollectionAsserter;
import com.dipasquale.data.structure.collection.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public final class DequeHashSetDefaultTest { // TODO: redo these test cases
    private static final DequeHashSet<String> TEST = new DequeHashSet<>();

    @BeforeEach
    public void beforeEach() {
        TEST.clear();
    }

    @Test
    public void TEST_1() {
        Assertions.assertEquals(0, TEST.size());
        Assertions.assertTrue(TEST.isEmpty());
        Assertions.assertFalse(TEST.contains("item-1"));
        Assertions.assertNull(TEST.getFirst());
        Assertions.assertNull(TEST.getLast());
    }

    @Test
    public void TEST_2() {
        Assertions.assertTrue(TEST.add("item-1"));
        Assertions.assertEquals(1, TEST.size());
        Assertions.assertFalse(TEST.isEmpty());
        Assertions.assertTrue(TEST.contains("item-1"));
        Assertions.assertEquals("item-1", TEST.getFirst());
        Assertions.assertEquals("item-1", TEST.getLast());
        Assertions.assertFalse(TEST.add("item-1"));
        Assertions.assertEquals(1, TEST.size());
        Assertions.assertFalse(TEST.isEmpty());
        Assertions.assertTrue(TEST.contains("item-1"));
        Assertions.assertEquals("item-1", TEST.getFirst());
        Assertions.assertEquals("item-1", TEST.getLast());
    }

    @Test
    public void TEST_3() {
        Assertions.assertTrue(TEST.addLast("item-1"));
        Assertions.assertFalse(TEST.add("item-1"));
    }

    @Test
    public void TEST_4() {
        Assertions.assertNull(TEST.getFirst());
        Assertions.assertTrue(TEST.add("item-1"));
        Assertions.assertEquals("item-1", TEST.getFirst());
    }

    @Test
    public void TEST_5() {
        Assertions.assertFalse(TEST.remove("item-1"));
        Assertions.assertTrue(TEST.add("item-1"));
        Assertions.assertTrue(TEST.remove("item-1"));
        Assertions.assertTrue(TEST.add("item-1"));
    }

    @Test
    public void TEST_6() {
        Assertions.assertNull(TEST.removeFirst());
        Assertions.assertTrue(TEST.add("item-1"));
        Assertions.assertEquals("item-1", TEST.removeFirst());
        Assertions.assertTrue(TEST.add("item-1"));
        Assertions.assertEquals("item-1", TEST.removeLast());
        Assertions.assertTrue(TEST.add("item-1"));
    }

    @Test
    public void TEST_7() {
        TEST.clear();
        Assertions.assertTrue(TEST.add("item-1"));
        Assertions.assertTrue(TEST.add("item-2"));
        Assertions.assertTrue(TEST.add("item-3"));
        Assertions.assertEquals(3, TEST.size());
        TEST.clear();
        Assertions.assertEquals(0, TEST.size());
    }

    @Test
    public void TEST_8() {
        Assertions.assertTrue(TEST.add("item-1"));
        Assertions.assertTrue(TEST.add("item-2"));
        Assertions.assertTrue(TEST.add("item-3"));
        Assertions.assertEquals(List.of("item-1", "item-2", "item-3"), List.copyOf(TEST));
    }

    @Test
    public void TEST_9() {
        Assertions.assertTrue(TEST.add("item-1"));
        Assertions.assertTrue(TEST.add("item-2"));
        Assertions.assertTrue(TEST.add("item-3"));
        Assertions.assertTrue(TEST.remove("item-2"));
        Assertions.assertTrue(TEST.add("item-2"));
        Assertions.assertEquals(List.of("item-1", "item-3", "item-2"), List.copyOf(TEST));
    }

    @Test
    public void TEST_10() {
        Assertions.assertTrue(TEST.add("item-1"));
        Assertions.assertTrue(TEST.add("item-2"));
        Assertions.assertTrue(TEST.add("item-3"));
        Assertions.assertEquals(List.of("item-3", "item-2", "item-1"), Lists.createCopyOf(TEST.descendingIterator()));
    }

    @Test
    public void TEST_11() {
        Assertions.assertTrue(TEST.add("item-1"));
        Assertions.assertTrue(TEST.add("item-2"));
        Assertions.assertTrue(TEST.add("item-3"));
        Assertions.assertTrue(TEST.remove("item-2"));
        Assertions.assertTrue(TEST.add("item-2"));
        Assertions.assertEquals(List.of("item-2", "item-3", "item-1"), Lists.createCopyOf(TEST.descendingIterator()));
    }

    @Test
    public void TEST_12() {
        CollectionAsserter<String> collectionAsserter = new CollectionAsserter<>(TEST, i -> String.format("item-%d", i), String.class, Assertions::assertEquals);

        collectionAsserter.assertToArray();
        collectionAsserter.assertContainsAll();
        collectionAsserter.assertAddAll();
        collectionAsserter.assertRetainAll();
        collectionAsserter.assertRemoveAll();
    }
}
