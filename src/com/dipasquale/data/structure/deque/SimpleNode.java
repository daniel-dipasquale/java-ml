package com.dipasquale.data.structure.deque;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public final class SimpleNode<T> implements Node {
    @EqualsAndHashCode.Include
    @ToString.Include
    final T value;
    final Object membership;
    SimpleNode<T> previous = null;
    SimpleNode<T> next = null;
}
