package com.java.lang;

import com.dipasquale.common.test.SerializableSupport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.function.Supplier;

public class SerializableTest {
    @Test
    public void TEST_1() {
        TestA1 a = new TestA1();
        TestB1 b = new TestB1();
        TestC1 c = new TestC1();

        a.b = b;
        b.c = c;
        c.a = a;

        try {
            byte[] bytes = SerializableSupport.serialize(a);
            TestA1 result = SerializableSupport.deserialize(bytes);

            Assertions.assertNotSame(a, result);
            Assertions.assertSame(result.b.c.a, result);
        } catch (IOException | ClassNotFoundException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void TEST_2() {
        TestA1 a = new TestA1();
        TestB1 b = new TestB1();
        TestC1 c = new TestC1();

        a.b = b;
        b.c = c;
        c.a = a;

        try {
            byte[] bytes = SerializableSupport.serialize(a);

            SerializableSupport.<TestA2>deserialize(bytes);
        } catch (ClassCastException e) {
        } catch (Throwable e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void TEST_3() {
        TestOptional test = new TestOptional();

        test.firstName = "Daniel";
        test.lastNameGetter = (Supplier<String> & Serializable) () -> "Di Pasquale";

        try {
            byte[] bytes = SerializableSupport.serialize(test);
            TestOptional result = SerializableSupport.deserialize(bytes);

            Assertions.assertEquals(test.firstName, result.firstName);
            Assertions.assertEquals(test.lastNameGetter.get(), result.lastNameGetter.get());
        } catch (IOException | ClassNotFoundException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class TestA1 implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        private TestB1 b;
    }

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class TestB1 implements Serializable {
        @Serial
        private static final long serialVersionUID = 2L;
        private TestC1 c;
    }

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class TestC1 implements Serializable {
        @Serial
        private static final long serialVersionUID = 3L;
        private TestA1 a;
    }

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class TestA2 implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        private TestB2 b;
    }

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class TestB2 implements Serializable {
        @Serial
        private static final long serialVersionUID = 2L;
        private TestC2 c;
    }

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class TestC2 implements Serializable {
        @Serial
        private static final long serialVersionUID = 3L;
        private TestA2 a;
    }

    private static final class TestOptional implements Serializable {
        @Serial
        private static final long serialVersionUID = -8948952928071862114L;
        private String firstName;
        private Supplier<String> lastNameGetter;

        @Serial
        private void readObject(final ObjectInputStream inputStream)
                throws IOException, ClassNotFoundException {
            firstName = (String) inputStream.readObject();

            try {
                lastNameGetter = (Supplier<String>) inputStream.readObject();
            } catch (Throwable e) {
                lastNameGetter = null;
            }
        }
    }
}
