package com.dipasquale.data.structure.set;

import com.dipasquale.data.structure.deque.StandardNode;
import com.dipasquale.data.structure.deque.StandardNodeDeque;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

public class DequeHashSet<T> extends AbstractDequeSet<T, StandardNode<T>> {
    @Serial
    private static final long serialVersionUID = -3104727285256676991L;

    private DequeHashSet(final Map<T, StandardNode<T>> nodesMap) {
        super(nodesMap, new StandardNodeDeque<>());
    }

    public DequeHashSet() {
        this(new HashMap<>());
    }

    public DequeHashSet(final int initialCapacity) {
        this(new HashMap<>(initialCapacity));
    }
}
