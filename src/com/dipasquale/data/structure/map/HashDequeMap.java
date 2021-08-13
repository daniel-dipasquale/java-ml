/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.map;

import com.dipasquale.data.structure.deque.SimpleNode;
import com.dipasquale.data.structure.deque.SimpleNodeDeque;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

public final class HashDequeMap<TKey, TValue> extends AbstractDequeMap<TKey, TValue, SimpleNode<Map.Entry<TKey, TValue>>> {
    @Serial
    private static final long serialVersionUID = 7909372707317851867L;

    private HashDequeMap(final Map<TKey, SimpleNode<Entry<TKey, TValue>>> nodesMap) {
        super(nodesMap, new SimpleNodeDeque<>());
    }

    public HashDequeMap() {
        this(new HashMap<>());
    }

    public HashDequeMap(final int initialCapacity) {
        this(new HashMap<>(initialCapacity));
    }
}
