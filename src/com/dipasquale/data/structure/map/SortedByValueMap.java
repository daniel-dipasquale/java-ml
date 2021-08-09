package com.dipasquale.data.structure.map;

import java.util.Map;
import java.util.Set;

public interface SortedByValueMap<TKey, TValue> extends Map<TKey, TValue> { // TODO: finish this interface
    Set<TKey> descendingKeySet();

    Map.Entry<TKey, TValue> headEntry();

    TKey headKey();

    TValue headValue();

    Map.Entry<TKey, TValue> tailEntry();

    TKey tailKey();

    TValue tailValue();
}
