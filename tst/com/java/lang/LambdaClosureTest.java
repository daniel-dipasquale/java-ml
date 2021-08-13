/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.java.lang;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

public final class LambdaClosureTest {
    private volatile String value = null;

    @BeforeEach
    public void beforeEach() {
        value = getClass().getSimpleName();
    }

    @Test
    public void TEST_1() {
        Supplier<String> supplier = () -> value;
        value = "TEST_1";
        Assertions.assertEquals("TEST_1", supplier.get());
        Assertions.assertEquals("TEST_1", value);
    }

    private static String pass(final String value) {
        return value;
    }

    @Test
    public void TEST_2() {
        Supplier<String> supplier = () -> pass(value);
        value = "TEST_2";
        Assertions.assertEquals("TEST_2", supplier.get());
        Assertions.assertEquals("TEST_2", value);
    }

    @RequiredArgsConstructor
    private static final class SupplierClosure implements Supplier<String> {
        private final String value;

        @Override
        public String get() {
            return value;
        }
    }

    @Test
    public void TEST_3() {
        Supplier<String> supplier = new SupplierClosure(value);

        value = "TEST_2";
        Assertions.assertEquals(getClass().getSimpleName(), supplier.get());
        Assertions.assertEquals("TEST_2", value);
    }
}
