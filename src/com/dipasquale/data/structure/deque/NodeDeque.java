/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.deque;

import java.util.Deque;

public interface NodeDeque<TValue, TNode extends Node> extends Deque<TNode> {
    TNode createUnbound(TValue value);

    TValue getValue(TNode node);

    TNode peekPrevious(TNode node);

    TNode peekNext(TNode node);

    boolean offerBefore(TNode node, TNode previousToNode);

    boolean offerAfter(TNode node, TNode nextToNode);

    void addBefore(TNode node, TNode previousToNode);

    void addAfter(TNode node, TNode nextToNode);

    static <TValue, TNode extends Node> NodeDeque<TValue, TNode> createSynchronized(final NodeDeque<TValue, TNode> nodeDeque) {
        return new SynchronizedNodeDeque<>(nodeDeque);
    }
}
