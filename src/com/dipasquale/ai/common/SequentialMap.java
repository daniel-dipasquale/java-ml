package com.dipasquale.ai.common;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;

public final class SequentialMap<TId extends Comparable<TId>, TItem> implements Iterable<TItem>, Serializable {
    @Serial
    private static final long serialVersionUID = 2209041596810668817L;
    private final List<ItemIdEntry<TId, TItem>> list = new ArrayList<>();
    private final NavigableMap<TId, TItem> navigableMap = new TreeMap<>(TId::compareTo);

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

    public boolean containsId(final TId id) {
        return navigableMap.containsKey(id);
    }

    public TItem getByIndex(final int index) {
        return list.get(index).item;
    }

    public TItem getById(final TId id) {
        return navigableMap.get(id);
    }

    public TItem getLast() {
        return Optional.ofNullable(navigableMap.lastEntry())
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    public TItem put(final TId id, final TItem item) {
        list.add(new ItemIdEntry<>(id, item));

        return navigableMap.put(id, item);
    }

    public TItem removeByIndex(final int index) {
        ItemIdEntry<TId, TItem> entry = list.remove(index); // TODO: rethink strategy for this method

        return navigableMap.remove(entry.id);
    }

    public TItem removeById(final TId id) {
        TItem item = navigableMap.remove(id);

        list.removeIf(e -> e.item == item); // TODO: rethink strategy for this method

        return item;
    }

    @Override
    public Iterator<TItem> iterator() {
        return list.stream()
                .map(e -> e.item)
                .iterator();
    }

    public Iterable<JointItems<TItem>> fullJoin(final SequentialMap<TId, TItem> other) {
        AscendingByIdIterator iterator = new AscendingByIdIterator(navigableMap.entrySet().iterator());
        AscendingByIdIterator otherIterator = new AscendingByIdIterator(other.navigableMap.entrySet().iterator());

        return () -> new JoinIterator(iterator, otherIterator);
    }

    @Generated
    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    @EqualsAndHashCode
    private static final class ItemIdEntry<TId, TItem> implements Serializable {
        @Serial
        private static final long serialVersionUID = -274541428462625603L;
        private final TId id;
        private final TItem item;
    }

    private final class AscendingByIdIterator implements Iterator<TId> {
        private final Iterator<Map.Entry<TId, TItem>> iterator;
        private boolean hasNext;
        private boolean moveNext;
        private Map.Entry<TId, TItem> entry;

        AscendingByIdIterator(final Iterator<Map.Entry<TId, TItem>> iterator) {
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

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private final class JoinIterator implements Iterator<JointItems<TItem>> {
        private final AscendingByIdIterator iterator1;
        private final AscendingByIdIterator iterator2;

        @Override
        public boolean hasNext() {
            return iterator1.hasNext() || iterator2.hasNext();
        }

        private JointItems<TItem> nextUsingBothIterators() {
            iterator1.moveNext = true;
            iterator2.moveNext = true;

            return new JointItems<>(iterator1.entry.getValue(), iterator2.entry.getValue());
        }

        private JointItems<TItem> nextUsingIterator1() {
            iterator1.moveNext = true;

            return new JointItems<>(iterator1.entry.getValue(), null);
        }

        private JointItems<TItem> nextUsingIterator2() {
            iterator2.moveNext = true;

            return new JointItems<>(null, iterator2.entry.getValue());
        }

        @Override
        public JointItems<TItem> next() {
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
    }
}
