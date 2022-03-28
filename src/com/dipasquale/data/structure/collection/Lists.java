package com.dipasquale.data.structure.collection;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Lists {
    public static <T> List<T> createCopyOf(final Iterator<T> iterator) {
        List<T> list = new ArrayList<>();

        while (iterator.hasNext()) {
            list.add(iterator.next());
        }

        return list;
    }

    public static <T> List<T> createCopyOf(final Iterable<T> iterable) {
        return createCopyOf(iterable.iterator());
    }

    @SafeVarargs
    public static <T> List<T> create(final T... values) {
        List<T> list = new ArrayList<>(values.length);

        Collections.addAll(list, values);

        return list;
    }
}
