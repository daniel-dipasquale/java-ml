package com.dipasquale.data.structure.group;

@FunctionalInterface
public interface ItemKeyAccessor<TKey, TItem> {
    TKey getKey(TItem item);
}
