package com.dipasquale.data.structure.set;

import com.dipasquale.data.structure.deque.SimpleNode;
import com.dipasquale.data.structure.deque.SimpleNodeDeque;

import java.io.Serial;
import java.util.IdentityHashMap;
import java.util.Map;

public final class DequeIdentitySet<T> extends AbstractDequeSet<T, SimpleNode<T>> {
    @Serial
    private static final long serialVersionUID = -1997695008715922153L;

    private DequeIdentitySet(final Map<T, SimpleNode<T>> nodesMap) {
        super(nodesMap, new SimpleNodeDeque<>());
    }

    public DequeIdentitySet() {
        this(new IdentityHashMap<>());
    }

    public DequeIdentitySet(final Iterable<T> iterable) {
        this();
        iterable.forEach(this::add);
    }
}
