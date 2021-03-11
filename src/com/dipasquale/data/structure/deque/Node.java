package com.dipasquale.data.structure.deque;

@FunctionalInterface
public interface Node {
    Object getMembership();

    boolean equals(Object other);

    int hashCode();

    String toString();
}
