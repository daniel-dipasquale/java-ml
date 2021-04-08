package com.dipasquale.data.structure.set;

import com.dipasquale.data.structure.deque.SimpleNode;
import com.dipasquale.data.structure.deque.SimpleNodeDeque;

import java.io.Serial;
import java.util.IdentityHashMap;
import java.util.Map;

public final class IdentityDequeSet<T> extends AbstractDequeSet<T, SimpleNode<T>> {
    @Serial
    private static final long serialVersionUID = -1997695008715922153L;

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
