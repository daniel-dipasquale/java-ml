package com.dipasquale.data.structure.map;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@FunctionalInterface
interface IteratorFactory<TKey, TValue> extends Serializable {
    Iterator<Map.Entry<TKey, TValue>> iterator();

    static <TKey, TValue> Stream<Map.Entry<TKey, TValue>> stream(final IteratorFactory<TKey, TValue> iteratorFactory) {
        Spliterator<Map.Entry<TKey, TValue>> entries = Spliterators.spliteratorUnknownSize(iteratorFactory.iterator(), 0);

        return StreamSupport.stream(entries, false);
    }
}
