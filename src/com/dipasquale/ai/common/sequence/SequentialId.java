package com.dipasquale.ai.common.sequence;

public interface SequentialId<T extends SequentialId<T>> extends Comparable<T> {
    int hashCode();

    boolean equals(Object other);

    String toString();
}
