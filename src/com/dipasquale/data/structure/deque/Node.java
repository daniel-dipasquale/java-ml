package com.dipasquale.data.structure.deque;

import java.io.Serializable;

public interface Node extends Serializable {
    boolean equals(Object other);

    int hashCode();

    String toString();
}
