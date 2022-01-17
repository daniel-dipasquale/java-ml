package com.dipasquale.common;

public interface IntegerValueFactory {
    IntegerValue create(int maximum, int offset);

    IntegerValue create(int maximum);
}
