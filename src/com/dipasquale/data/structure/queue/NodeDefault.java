package com.dipasquale.data.structure.queue;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
final class NodeDefault<TValue> implements Node {
    @EqualsAndHashCode.Include
    @ToString.Include
    final TValue value;
    @Getter
    final Object membership;
    NodeDefault<TValue> previous = null;
    NodeDefault<TValue> next = null;
}
