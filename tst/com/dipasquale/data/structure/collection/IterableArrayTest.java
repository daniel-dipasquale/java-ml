package com.dipasquale.data.structure.collection;

import com.dipasquale.common.factory.ObjectIndexProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public final class IterableArrayTest {
    @Test
    public void TEST_1() {
        IterableArray<Integer> test = new IterableArray<>(3);

        Assertions.assertEquals(0, test.size());
        Assertions.assertEquals(3, test.capacity());
        Assertions.assertEquals(List.of(), ListSupport.copyOf(test));
    }

    @Test
    public void TEST_2() {
        IterableArray<Integer> test = new IterableArray<>(3);

        Assertions.assertNull(test.get(0));
        Assertions.assertNull(test.get(1));
        Assertions.assertNull(test.get(2));
        test.put(1, 1);
        Assertions.assertEquals(1, test.get(1));
    }

    @Test
    public void TEST_3() {
        IterableArray<Integer> test = new IterableArray<>(3);

        Assertions.assertNull(test.put(2, 2));
        Assertions.assertEquals(1, test.size());
        Assertions.assertNull(test.put(0, 0));
        Assertions.assertEquals(2, test.size());
        Assertions.assertNull(test.put(1, 1));
        Assertions.assertEquals(3, test.size());

        Assertions.assertEquals(ListSupport.<Integer>builder()
                .add(2)
                .add(0)
                .add(1)
                .build(), ListSupport.copyOf(test));
    }

    @Test
    public void TEST_4() {
        IterableArray<String> test = new IterableArray<>(3);

        test.put(0, "0.1");
        test.put(1, "1.1");
        Assertions.assertEquals("1.1", test.put(1, "1.2"));
        Assertions.assertEquals(2, test.size());
        Assertions.assertNull(test.put(2, "2.1"));
        Assertions.assertEquals(3, test.size());

        Assertions.assertEquals(ListSupport.<String>builder()
                .add("0.1")
                .add("1.2")
                .add("2.1")
                .build(), ListSupport.copyOf(test));
    }

    @Test
    public void TEST_5() {
        IterableArray<String> test = new IterableArray<>(3);

        test.put(0, "0.1");
        test.put(1, "1.1");
        test.put(2, "2.1");
        Assertions.assertEquals("1.1", test.put(1, "1.2"));
        Assertions.assertEquals(3, test.size());

        Assertions.assertEquals(ListSupport.<String>builder()
                .add("0.1")
                .add("1.2")
                .add("2.1")
                .build(), ListSupport.copyOf(test));
    }

    @Test
    public void TEST_6() {
        IterableArray<String> test = new IterableArray<>(3);

        Assertions.assertNull(test.remove(0));
        Assertions.assertEquals(0, test.size());
        Assertions.assertEquals(List.of(), ListSupport.copyOf(test));
    }

    @Test
    public void TEST_7() {
        IterableArray<String> test = new IterableArray<>(3);

        test.put(0, "0.1");
        test.put(1, "1.1");
        Assertions.assertEquals("1.1", test.remove(1));
        Assertions.assertEquals(1, test.size());
        test.put(1, "1.2");
        Assertions.assertEquals(2, test.size());
        test.put(2, "2.1");
        Assertions.assertEquals(3, test.size());
        Assertions.assertEquals("1.2", test.remove(1));
        Assertions.assertEquals(2, test.size());

        Assertions.assertEquals(ListSupport.<String>builder()
                .add("0.1")
                .add("2.1")
                .build(), ListSupport.copyOf(test));
    }

    @Test
    public void TEST_8() {
        IterableArray<Integer> test = new IterableArray<>(3);

        test.put(0, 0);
        test.put(1, 1);
        test.put(2, 2);
        Assertions.assertEquals(0, test.remove(0));
        Assertions.assertEquals(2, test.size());
        Assertions.assertEquals(1, test.remove(1));
        Assertions.assertEquals(1, test.size());
        Assertions.assertEquals(2, test.remove(2));
        Assertions.assertEquals(0, test.size());
        Assertions.assertEquals(List.of(), ListSupport.copyOf(test));
        Assertions.assertNull(test.put(2, 2));
        Assertions.assertEquals(1, test.size());

        Assertions.assertEquals(ListSupport.<Integer>builder()
                .add(2)
                .build(), ListSupport.copyOf(test));
    }

    @Test
    public void TEST_9() {
        IterableArray<Integer> test = new IterableArray<>(3);

        test.put(0, 0);
        test.put(1, 1);
        test.put(2, 2);
        test.clear();
        Assertions.assertEquals(0, test.size());
        Assertions.assertEquals(List.of(), ListSupport.copyOf(test));
        Assertions.assertNull(test.put(2, 2));
        Assertions.assertEquals(1, test.size());

        Assertions.assertEquals(ListSupport.<Integer>builder()
                .add(2)
                .build(), ListSupport.copyOf(test));
    }

    @Test
    public void TEST_10() {
        IterableArray<String> test = new IterableArray<>(3);

        Assertions.assertEquals("0.1", test.compute(0, oldElement -> "0.1"));
        Assertions.assertEquals(1, test.size());
        Assertions.assertEquals("1.1", test.compute(1, oldElement -> "1.1"));
        Assertions.assertEquals(2, test.size());
        Assertions.assertNull(test.compute(0, oldElement -> null));
        Assertions.assertEquals(1, test.size());

        Assertions.assertEquals(ListSupport.<String>builder()
                .add("1.1")
                .build(), ListSupport.copyOf(test));
    }

    @Test
    public void TEST_11() {
        IterableArray<String> test = new IterableArray<>(3);

        test.put(0, "0.1");
        test.put(1, "1.1");
        test.put(2, "2.1");
        Assertions.assertEquals(List.of(), test.resize(3, null));
        Assertions.assertEquals(3, test.size());
        Assertions.assertEquals(3, test.capacity());

        Assertions.assertEquals(ListSupport.<String>builder()
                .add("0.1")
                .add("1.1")
                .add("2.1")
                .build(), ListSupport.copyOf(test));
    }

    @Test
    public void TEST_12() {
        IterableArray<String> test = new IterableArray<>(3);

        test.put(0, "0.1");
        test.put(1, "1.1");
        test.put(2, "2.1");

        Assertions.assertEquals(ListSupport.<String>builder()
                .add("0.1")
                .add("1.1")
                .build(), test.resize(1, null));

        Assertions.assertEquals(1, test.size());
        Assertions.assertEquals(1, test.capacity());

        Assertions.assertEquals(ListSupport.<String>builder()
                .add("0.1")
                .build(), ListSupport.copyOf(test));
    }

    @Test
    public void TEST_13() {
        IterableArray<String> test = new IterableArray<>(3);

        test.put(0, "0.1");
        test.put(1, "1.1");
        test.put(2, "2.1");
        Assertions.assertEquals(List.of(), test.resize(5, null));
        Assertions.assertEquals(3, test.size());
        Assertions.assertEquals(5, test.capacity());

        Assertions.assertEquals(ListSupport.<String>builder()
                .add("0.1")
                .add("1.1")
                .add("2.1")
                .build(), ListSupport.copyOf(test));
    }

    @Test
    public void TEST_14() {
        IterableArray<String> test = new IterableArray<>(3);

        ObjectIndexProvider<String> resizeObjectProvider = index -> index % 2 == 0
                ? String.format("%d.1", index)
                : null;

        test.put(0, "0.1");
        test.put(1, "1.1");
        test.put(2, "2.1");

        Assertions.assertEquals(List.of(), test.resize(5, resizeObjectProvider));
        Assertions.assertEquals(4, test.size());
        Assertions.assertEquals(5, test.capacity());

        Assertions.assertEquals(ListSupport.<String>builder()
                .add("0.1")
                .add("1.1")
                .add("2.1")
                .add("4.1")
                .build(), ListSupport.copyOf(test));
    }
}
