package com.dipasquale.data.structure.map;

import java.io.Serializable;
import java.util.Map;

public interface DequeMap<TKey, TValue> extends Map<TKey, TValue>, Serializable {
    TValue putFirst(TKey key, TValue value);

    TValue putLast(TKey key, TValue value);

    TValue removeFirst();

    TValue removeLast();

    Entry<TKey, TValue> withdrawFirst();

    Entry<TKey, TValue> withdrawLast();
}
