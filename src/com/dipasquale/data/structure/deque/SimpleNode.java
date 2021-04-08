package com.dipasquale.data.structure.deque;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public final class SimpleNode<T> implements Node {
    @Serial
    private static final long serialVersionUID = 7550176372159511557L;
    @EqualsAndHashCode.Include
    @ToString.Include
    final T value;
    final Object membership;
    SimpleNode<T> previous = null;
    SimpleNode<T> next = null;
}
