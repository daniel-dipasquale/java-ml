/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

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
public final class SimpleNode<T> implements Node, Serializable {
    @Serial
    private static final long serialVersionUID = -5328023116496736922L;
    @EqualsAndHashCode.Include
    @ToString.Include
    final T value;
    final Object membership;
    SimpleNode<T> previous = null;
    SimpleNode<T> next = null;
}
