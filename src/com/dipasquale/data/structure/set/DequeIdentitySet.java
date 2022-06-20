package com.dipasquale.data.structure.set;

import com.dipasquale.data.structure.deque.StandardNode;
import com.dipasquale.data.structure.deque.StandardNodeDeque;

import java.io.Serial;
import java.util.IdentityHashMap;
import java.util.Map;

public final class DequeIdentitySet<T> extends AbstractDequeSet<T, StandardNode<T>> {
    @Serial
    private static final long serialVersionUID = -1997695008715922153L;

    private DequeIdentitySet(final Map<T, StandardNode<T>> nodesMap) {
        super(nodesMap, new StandardNodeDeque<>());
    }

    public DequeIdentitySet() {
        this(new IdentityHashMap<>());
    }

    public DequeIdentitySet(final Iterable<T> iterable) {
        this();
        iterable.forEach(this::add);
    }
}
