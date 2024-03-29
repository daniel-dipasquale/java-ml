package com.dipasquale.data.structure.deque;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public final class StandardNode<T> implements Node, Serializable {
    @Serial
    private static final long serialVersionUID = -5328023116496736922L;
    final Object membership;
    @EqualsAndHashCode.Include
    @ToString.Include
    final T value;
    StandardNode<T> previous = null;
    StandardNode<T> next = null;
}
