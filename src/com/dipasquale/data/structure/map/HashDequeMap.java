package com.dipasquale.data.structure.map;

import com.dipasquale.data.structure.deque.StandardNode;
import com.dipasquale.data.structure.deque.StandardNodeDeque;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

public final class HashDequeMap<TKey, TValue> extends AbstractDequeMap<TKey, TValue, StandardNode<Map.Entry<TKey, TValue>>> {
    @Serial
    private static final long serialVersionUID = 7909372707317851867L;

    private HashDequeMap(final Map<TKey, StandardNode<Entry<TKey, TValue>>> nodesMap) {
        super(nodesMap, new StandardNodeDeque<>());
    }

    public HashDequeMap() {
        this(new HashMap<>());
    }

    public HashDequeMap(final int initialCapacity) {
        this(new HashMap<>(initialCapacity));
    }
}
