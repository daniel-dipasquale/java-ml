/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.deque;

public interface Node {
    boolean equals(Object other);

    int hashCode();

    String toString();
}
