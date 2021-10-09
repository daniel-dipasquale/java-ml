package com.dipasquale.data.structure.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public interface Lists {
    static <T> List<T> copyOf(final Iterator<T> iterator) {
        List<T> list = new ArrayList<>();

        while (iterator.hasNext()) {
            list.add(iterator.next());
        }

        return list;
    }

    static <T> List<T> copyOf(final Iterable<T> iterable) {
        return copyOf(iterable.iterator());
    }

    @SafeVarargs
    static <T> List<T> create(final T... values) {
        List<T> list = new ArrayList<>(values.length);

        Collections.addAll(list, values);

        return list;
    }
}
