package com.dipasquale.data.structure.collection;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ListSupport {
    public static <T> List<T> copyOf(final Iterator<T> iterator) {
        List<T> list = new ArrayList<>();

        while (iterator.hasNext()) {
            list.add(iterator.next());
        }

        return list;
    }

    public static <T> List<T> copyOf(final Iterable<T> iterable) {
        return copyOf(iterable.iterator());
    }

    @SafeVarargs
    public static <T> List<T> create(final T... values) {
        List<T> list = new ArrayList<>(values.length);

        Collections.addAll(list, values);

        return list;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static final class Builder<T> {
        private final List<T> list = new ArrayList<>();

        public Builder<T> add(final T item) {
            list.add(item);

            return this;
        }

        public List<T> build() {
            return ListSupport.copyOf(list);
        }
    }
}
