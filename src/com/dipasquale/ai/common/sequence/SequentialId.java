/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.common.sequence;

public interface SequentialId extends Comparable<SequentialId> {
    int hashCode();

    boolean equals(Object other);

    String toString();
}
