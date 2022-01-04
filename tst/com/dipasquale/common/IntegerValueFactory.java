package com.dipasquale.common;

public interface IntegerValueFactory {
    IntegerValue create(final int max, final int offset);

    IntegerValue create(final int max);
}
