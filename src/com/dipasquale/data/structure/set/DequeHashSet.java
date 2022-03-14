package com.dipasquale.data.structure.set;

import com.dipasquale.data.structure.deque.PlainNode;
import com.dipasquale.data.structure.deque.PlainNodeDeque;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

public class DequeHashSet<T> extends AbstractDequeSet<T, PlainNode<T>> {
    @Serial
    private static final long serialVersionUID = -3104727285256676991L;

    private DequeHashSet(final Map<T, PlainNode<T>> nodesMap) {
        super(nodesMap, new PlainNodeDeque<>());
    }

    public DequeHashSet() {
        this(new HashMap<>());
    }

    public DequeHashSet(final int initialCapacity) {
        this(new HashMap<>(initialCapacity));
    }
}
