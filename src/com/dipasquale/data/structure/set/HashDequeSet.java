package com.dipasquale.data.structure.set;

import com.dipasquale.data.structure.deque.SimpleNode;
import com.dipasquale.data.structure.deque.SimpleNodeDeque;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

public class HashDequeSet<T> extends AbstractDequeSet<T, SimpleNode<T>> {
    @Serial
    private static final long serialVersionUID = -3104727285256676991L;

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
