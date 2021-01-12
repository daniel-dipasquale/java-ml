package com.dipasquale.data.structure.queue;

@FunctionalInterface
public interface Node {
    Object getMembership();

    boolean equals(Object other);

    int hashCode();

    String toString();
}
