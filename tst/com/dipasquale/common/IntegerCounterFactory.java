package com.dipasquale.common;

public interface IntegerCounterFactory {
    IntegerCounter create(final int max, final int offset);

    IntegerCounter create(final int max);
}
