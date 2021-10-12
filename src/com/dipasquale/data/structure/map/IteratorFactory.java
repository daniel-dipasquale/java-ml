package com.dipasquale.data.structure.map;

import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@FunctionalInterface
interface IteratorFactory<TKey, TValue> {
    Iterator<Map.Entry<TKey, TValue>> createIterator();

    default Stream<Map.Entry<TKey, TValue>> createStream() {
        Spliterator<Map.Entry<TKey, TValue>> entries = Spliterators.spliteratorUnknownSize(createIterator(), 0);

        return StreamSupport.stream(entries, false);
    }
}
