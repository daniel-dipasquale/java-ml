package com.dipasquale.data.structure.group;

@FunctionalInterface
public interface ElementKeyAccessor<TKey, TElement> {
    TKey getKey(TElement element);
}
