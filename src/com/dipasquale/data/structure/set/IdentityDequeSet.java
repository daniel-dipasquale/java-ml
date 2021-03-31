package com.dipasquale.data.structure.set;

import com.dipasquale.data.structure.deque.SimpleNode;
import com.dipasquale.data.structure.deque.SimpleNodeDeque;

import java.util.IdentityHashMap;
import java.util.Map;

public final class IdentityDequeSet<T> extends AbstractDequeSet<T, SimpleNode<T>> {
    private IdentityDequeSet(final Map<T, SimpleNode<T>> nodesMap) {
        super(nodesMap, new SimpleNodeDeque<>());
    }

    public IdentityDequeSet() {
        this(new IdentityHashMap<>());
    }

    public IdentityDequeSet(final Iterable<T> iterable) {
        this();
        iterable.forEach(this::add);
    }
}
