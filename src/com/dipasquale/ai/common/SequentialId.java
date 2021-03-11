package com.dipasquale.ai.common;

public interface SequentialId extends Comparable<SequentialId> {
    int hashCode();

    boolean equals(Object other);
}
