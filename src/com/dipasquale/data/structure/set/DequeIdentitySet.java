package com.dipasquale.data.structure.set;

import com.dipasquale.data.structure.deque.PlainNode;
import com.dipasquale.data.structure.deque.PlainNodeDeque;

import java.io.Serial;
import java.util.IdentityHashMap;
import java.util.Map;

public final class DequeIdentitySet<T> extends AbstractDequeSet<T, PlainNode<T>> {
    @Serial
    private static final long serialVersionUID = -1997695008715922153L;

    private DequeIdentitySet(final Map<T, PlainNode<T>> nodesMap) {
        super(nodesMap, new PlainNodeDeque<>());
    }

    public DequeIdentitySet() {
        this(new IdentityHashMap<>());
    }

    public DequeIdentitySet(final Iterable<T> iterable) {
        this();
        iterable.forEach(this::add);
    }
}
