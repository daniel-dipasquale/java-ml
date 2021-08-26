package com.dipasquale.common;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.io.Serializable;

@RequiredArgsConstructor
public final class CyclicIntegerCounterTestHarness {
    private final IntegerCounterFactory integerCounterFactory;

    public void assertContinuousIncrementAndCurrent() {
        IntegerCounter test = integerCounterFactory.create(3);

        for (int i = 0; i < 2; i++) {
            Assertions.assertEquals(0, test.increment());
            Assertions.assertEquals(0, test.current());
            Assertions.assertEquals(1, test.increment());
            Assertions.assertEquals(1, test.current());
            Assertions.assertEquals(2, test.increment());
            Assertions.assertEquals(2, test.current());
        }
    }

    public void assertContinuousDecrementAndCurrent() {
        IntegerCounter test = integerCounterFactory.create(3, 0);

        for (int i = 0; i < 2; i++) {
            Assertions.assertEquals(2, test.decrement());
            Assertions.assertEquals(2, test.current());
            Assertions.assertEquals(1, test.decrement());
            Assertions.assertEquals(1, test.current());
            Assertions.assertEquals(0, test.decrement());
            Assertions.assertEquals(0, test.current());
        }
    }

    public void assertCyclicIncrementAndCurrent() {
        IntegerCounter test = integerCounterFactory.create(3);

        for (int i = 0; i < 2; i++) {
            Assertions.assertEquals(0, test.increment());
            Assertions.assertEquals(0, test.current());
            Assertions.assertEquals(2, test.decrement());
            Assertions.assertEquals(2, test.current());
        }
    }

    public void assertCurrentGetAndSet() {
        IntegerCounter test = integerCounterFactory.create(3, 0);

        for (int i = 0; i < 6; i++) {
            Assertions.assertEquals(i % 3, test.current(i));
            Assertions.assertEquals(i % 3, test.current());
        }
    }

    public void assertCompareTo() {
        IntegerCounter test = integerCounterFactory.create(3, 0);

        Assertions.assertEquals(1, test.current(1));
        Assertions.assertEquals(Integer.compare(1, 0), test.compareTo(0));
        Assertions.assertEquals(Integer.compare(1, 1), test.compareTo(1));
        Assertions.assertEquals(Integer.compare(1, 2), test.compareTo(-1));
        Assertions.assertEquals(Integer.compare(1, 2), test.compareTo(2));
        Assertions.assertEquals(Integer.compare(1, 1), test.compareTo(-2));
        Assertions.assertEquals(Integer.compare(1, 0), test.compareTo(3));
        Assertions.assertEquals(Integer.compare(1, 0), test.compareTo(-3));
        Assertions.assertEquals(Integer.compare(1, 1), test.compareTo(4));
        Assertions.assertEquals(Integer.compare(1, 2), test.compareTo(-4));
        Assertions.assertEquals(Integer.compare(1, 2), test.compareTo(5));
        Assertions.assertEquals(Integer.compare(1, 1), test.compareTo(-5));
    }

    public void assertEqualsAndHashCode() {
        IntegerCounter test1 = integerCounterFactory.create(3, 0);
        IntegerCounter test2 = integerCounterFactory.create(3);

        Assertions.assertNotEquals(test1, test2);
        Assertions.assertEquals(1, test1.current(1));
        Assertions.assertEquals(1, test2.current(2));
        Assertions.assertEquals(test1, test2);
    }

    public void assertToString() {
        IntegerCounter test = integerCounterFactory.create(3, 0);

        Assertions.assertEquals(0, test.current(0));
        Assertions.assertEquals("0", test.toString());
        Assertions.assertEquals(1, test.current(1));
        Assertions.assertEquals("1", test.toString());
    }

    public void assertSerialization() {
        IntegerCounter test1 = integerCounterFactory.create(3, 0);

        if (test1 instanceof Serializable) {
            Assertions.assertEquals(2, test1.current(2));

            try {
                byte[] test1Bytes = SerializableSupport.serialize((Serializable) test1);
                IntegerCounter test2 = SerializableSupport.deserialize(test1Bytes);

                Assertions.assertNotSame(test1, test2);

                for (int i = 0; i < 6; i++) {
                    Assertions.assertEquals(test1.increment(), test2.increment());
                    Assertions.assertEquals(test1, test2);
                }
            } catch (IOException | ClassNotFoundException e) {
                Assertions.fail(e);
            }
        }
    }
}
