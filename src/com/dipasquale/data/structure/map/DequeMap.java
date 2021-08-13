/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.map;

import java.util.Map;

public interface DequeMap<TKey, TValue> extends Map<TKey, TValue> {
    TValue putFirst(TKey key, TValue value);

    TValue putLast(TKey key, TValue value);

    TValue removeFirst();

    TValue removeLast();

    Entry<TKey, TValue> withdrawFirst();

    Entry<TKey, TValue> withdrawLast();
}
