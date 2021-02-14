package com.dipasquale.data.structure.set;

import com.dipasquale.common.test.ThrowableAsserter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.NoSuchElementException;

public final class InsertOrderSetDefaultTest {
    private static final InsertOrderSetDefault<String> TEST = new InsertOrderSetDefault<>();

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
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(NoSuchElementException.class)
                    .build(), ThrowableAsserter.create(e));
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
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(NoSuchElementException.class)
                    .build(), ThrowableAsserter.create(e));
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
    public void TEST_8() {
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
    public void TEST_9() {
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
    public void TEST_10() {
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
    public void TEST_11() {
        Assert.assertTrue(TEST.add("item-1"));
        Assert.assertTrue(TEST.add("item-2"));
        Assert.assertTrue(TEST.add("item-3"));
        Assert.assertTrue(TEST.add("item-4"));
        Assert.assertTrue(TEST.add("item-5"));

        Assert.assertTrue(TEST.retainAll(ImmutableList.<String>builder()
                .add("item-2")
                .add("item-3")
                .build()));

        Assert.assertEquals(ImmutableList.builder()
                .add("item-2")
                .add("item-3")
                .build(), ImmutableList.copyOf(TEST));
    }

    @Test
    public void TEST_12() {
        Assert.assertTrue(TEST.add("item-1"));
        Assert.assertTrue(TEST.add("item-2"));
        Assert.assertTrue(TEST.add("item-3"));
        Assert.assertTrue(TEST.add("item-4"));
        Assert.assertTrue(TEST.add("item-5"));

        Assert.assertTrue(TEST.retainAll(ImmutableSet.<String>builder()
                .add("item-6")
                .add("item-7")
                .build()));

        Assert.assertEquals(ImmutableList.of(), ImmutableList.copyOf(TEST));
    }

    @Test
    public void TEST_13() {
        Assert.assertTrue(TEST.add("item-1"));
        Assert.assertTrue(TEST.add("item-2"));
        Assert.assertTrue(TEST.add("item-3"));
        Assert.assertTrue(TEST.add("item-4"));
        Assert.assertTrue(TEST.add("item-5"));

        Assert.assertTrue(TEST.retainAll(ImmutableList.<String>builder()
                .add("item-1")
                .add("item-7")
                .build()));

        Assert.assertEquals(ImmutableList.builder()
                .add("item-1")
                .build(), ImmutableList.copyOf(TEST));
    }

    @Test
    public void TEST_14() {
        Assert.assertTrue(TEST.add("item-1"));
        Assert.assertTrue(TEST.add("item-2"));
        Assert.assertTrue(TEST.add("item-3"));
        Assert.assertTrue(TEST.add("item-4"));
        Assert.assertTrue(TEST.add("item-5"));

        Assert.assertFalse(TEST.retainAll(ImmutableList.<String>builder()
                .add("item-1")
                .add("item-2")
                .add("item-3")
                .add("item-4")
                .add("item-5")
                .build()));

        Assert.assertEquals(ImmutableList.builder()
                .add("item-1")
                .add("item-2")
                .add("item-3")
                .add("item-4")
                .add("item-5")
                .build(), ImmutableList.copyOf(TEST));
    }

    @Test
    public void TEST_15() {
        Assert.assertTrue(TEST.add("item-1"));
        Assert.assertTrue(TEST.add("item-2"));
        Assert.assertTrue(TEST.add("item-3"));
        Assert.assertTrue(TEST.add("item-4"));
        Assert.assertTrue(TEST.add("item-5"));

        Assert.assertTrue(TEST.removeAll(ImmutableList.<String>builder()
                .add("item-2")
                .add("item-3")
                .build()));

        Assert.assertEquals(ImmutableList.builder()
                .add("item-1")
                .add("item-4")
                .add("item-5")
                .build(), ImmutableList.copyOf(TEST));
    }

    @Test
    public void TEST_16() {
        Assert.assertTrue(TEST.add("item-1"));
        Assert.assertTrue(TEST.add("item-2"));
        Assert.assertTrue(TEST.add("item-3"));
        Assert.assertTrue(TEST.add("item-4"));
        Assert.assertTrue(TEST.add("item-5"));

        Assert.assertFalse(TEST.removeAll(ImmutableList.<String>builder()
                .add("item-6")
                .add("item-7")
                .build()));

        Assert.assertEquals(ImmutableList.builder()
                .add("item-1")
                .add("item-2")
                .add("item-3")
                .add("item-4")
                .add("item-5")
                .build(), ImmutableList.copyOf(TEST));
    }

    @Test
    public void TEST_17() {
        Assert.assertTrue(TEST.add("item-1"));
        Assert.assertTrue(TEST.add("item-2"));
        Assert.assertTrue(TEST.add("item-3"));
        Assert.assertTrue(TEST.add("item-4"));
        Assert.assertTrue(TEST.add("item-5"));

        Assert.assertTrue(TEST.removeAll(ImmutableList.<String>builder()
                .add("item-1")
                .add("item-7")
                .build()));

        Assert.assertEquals(ImmutableList.builder()
                .add("item-2")
                .add("item-3")
                .add("item-4")
                .add("item-5")
                .build(), ImmutableList.copyOf(TEST));
    }

    @Test
    public void TEST_18() {
        Assert.assertTrue(TEST.add("item-1"));
        Assert.assertTrue(TEST.add("item-2"));
        Assert.assertTrue(TEST.add("item-3"));
        Assert.assertTrue(TEST.add("item-4"));
        Assert.assertTrue(TEST.add("item-5"));

        Assert.assertTrue(TEST.removeAll(ImmutableList.<String>builder()
                .add("item-1")
                .add("item-2")
                .add("item-3")
                .add("item-4")
                .add("item-5")
                .build()));

        Assert.assertEquals(ImmutableList.of(), ImmutableList.copyOf(TEST));
    }
}
