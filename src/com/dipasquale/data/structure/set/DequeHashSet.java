/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.set;

import com.dipasquale.data.structure.deque.SimpleNode;
import com.dipasquale.data.structure.deque.SimpleNodeDeque;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

public class DequeHashSet<T> extends AbstractDequeSet<T, SimpleNode<T>> {
    @Serial
    private static final long serialVersionUID = -3104727285256676991L;

    private DequeHashSet(final Map<T, SimpleNode<T>> nodesMap) {
        super(nodesMap, new SimpleNodeDeque<>());
    }

    public DequeHashSet() {
        this(new HashMap<>());
    }

    public DequeHashSet(final int initialCapacity) {
        this(new HashMap<>(initialCapacity));
    }
}
