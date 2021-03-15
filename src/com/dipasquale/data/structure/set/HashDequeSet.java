package com.dipasquale.data.structure.set;

import com.dipasquale.data.structure.deque.SimpleNode;
import com.dipasquale.data.structure.deque.SimpleNodeDeque;

import java.util.HashMap;
import java.util.Map;

public class HashDequeSet<T> extends AbstractDequeSet<T, SimpleNode<T>> {
    private HashDequeSet(final Map<T, SimpleNode<T>> nodesMap) {
        super(nodesMap, new SimpleNodeDeque<>());
    }

    public HashDequeSet() {
        this(new HashMap<>());
    }

    public HashDequeSet(final int initialCapacity) {
        this(new HashMap<>(initialCapacity));
    }
}
