package com.dipasquale.data.structure.iterable;

import com.dipasquale.data.structure.iterator.FlatIterator;
import com.dipasquale.data.structure.iterator.ZipIterator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class IterableSupport {
    @SafeVarargs
    public static <T> Iterable<T> concatenate(final Iterable<T>... iterables) {
        return () -> FlatIterator.fromIterables(Arrays.stream(iterables)::iterator);
    }

    public static int hashCode(final Iterable<?> iterable) {
        int hashCode = 1;

        for (Object element : iterable) {
            hashCode = 31 * hashCode + (element == null ? 0 : element.hashCode());
        }

        return hashCode;
    }

    public static <T> boolean equals(final Iterable<T> iterable1, final Iterable<T> iterable2) {
        List<Iterator<T>> iterators = List.of(iterable1.iterator(), iterable2.iterator());
        ZipIterator<T> iterator = new ZipIterator<>(iterators);

        while (iterator.hasNext()) {
            List<T> elements = iterator.next();

            if (!Objects.equals(elements.get(0), elements.get(1))) {
                return false;
            }
        }

        return true;
    }

    public static String toString(final Iterable<?> iterable) {
        Iterator<?> iterator = iterable.iterator();

        if (!iterator.hasNext()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        int elements = 0;

        sb.append('[');

        do {
            if (elements++ > 0) {
                sb.append(',');
                sb.append(' ');
            }

            Object element = iterator.next();

            sb.append(element == iterable ? "(this Collection)" : element);
        } while (iterator.hasNext());

        sb.append(']');

        return sb.toString();
    }
}
