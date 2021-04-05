package com.dipasquale.ai.common;

import java.io.Serializable;

public interface SequentialId extends Comparable<SequentialId>, Serializable {
    int hashCode();

    boolean equals(Object other);

    String toString();
}
