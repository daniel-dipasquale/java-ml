package com.dipasquale.data.structure.group;

import com.dipasquale.common.Pair;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ListSetGroupIterator<TId extends Comparable<TId>, TElement> implements Iterator<Pair<TElement>> {
    private final AscendingByIdIterator iterator1;
    private final AscendingByIdIterator iterator2;

    ListSetGroupIterator(final NavigableMap<TId, TElement> navigableMap1, final NavigableMap<TId, TElement> navigableMap2) {
        this.iterator1 = new AscendingByIdIterator(navigableMap1.entrySet().iterator());
        this.iterator2 = new AscendingByIdIterator(navigableMap2.entrySet().iterator());
    }

    @Override
    public boolean hasNext() {
        return iterator1.hasNext() || iterator2.hasNext();
    }

    private Pair<TElement> nextUsingBothIterators() {
        iterator1.moveNext = true;
        iterator2.moveNext = true;

        return new Pair<>(iterator1.entry.getValue(), iterator2.entry.getValue());
    }

    private Pair<TElement> nextUsingIterator1() {
        iterator1.moveNext = true;

        return new Pair<>(iterator1.entry.getValue(), null);
    }

    private Pair<TElement> nextUsingIterator2() {
        iterator2.moveNext = true;

        return new Pair<>(null, iterator2.entry.getValue());
    }

    @Override
    public Pair<TElement> next() {
        TId id1 = iterator1.next();
        TId id2 = iterator2.next();

        if (id1 != null && id2 != null) {
            int comparison = id1.compareTo(id2);

            if (comparison == 0) {
                return nextUsingBothIterators();
            }

            if (comparison < 0) {
                return nextUsingIterator1();
            }

            return nextUsingIterator2();
        }

        if (id1 != null) {
            return nextUsingIterator1();
        }

        return nextUsingIterator2();
    }

    private final class AscendingByIdIterator implements Iterator<TId> {
        private final Iterator<Map.Entry<TId, TElement>> iterator;
        private boolean hasNext;
        private boolean moveNext;
        private Map.Entry<TId, TElement> entry;

        AscendingByIdIterator(final Iterator<Map.Entry<TId, TElement>> iterator) {
            this.iterator = iterator;
            this.hasNext = iterator.hasNext();
            this.moveNext = true;
            this.entry = null;
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public TId next() {
            if (moveNext) {
                moveNext = false;

                if (hasNext) {
                    entry = iterator.next();
                    hasNext = iterator.hasNext();
                } else {
                    entry = null;
                }
            }

            if (entry == null) {
                return null;
            }

            return entry.getKey();
        }
    }
}
