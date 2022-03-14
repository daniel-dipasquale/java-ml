package com.dipasquale.data.structure.map;

import com.dipasquale.data.structure.deque.PlainNode;
import com.dipasquale.data.structure.deque.PlainNodeDeque;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

public final class HashDequeMap<TKey, TValue> extends AbstractDequeMap<TKey, TValue, PlainNode<Map.Entry<TKey, TValue>>> {
    @Serial
    private static final long serialVersionUID = 7909372707317851867L;

    private HashDequeMap(final Map<TKey, PlainNode<Entry<TKey, TValue>>> nodesMap) {
        super(nodesMap, new PlainNodeDeque<>());
    }

    public HashDequeMap() {
        this(new HashMap<>());
    }

    public HashDequeMap(final int initialCapacity) {
        this(new HashMap<>(initialCapacity));
    }
}
