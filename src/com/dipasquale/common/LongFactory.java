package com.dipasquale.common;

import java.io.Serializable;

@FunctionalInterface
public interface LongFactory extends Serializable {
    long create();
}
