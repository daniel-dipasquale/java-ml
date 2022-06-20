package com.dipasquale.search.mcts;

import com.dipasquale.common.Record;
import com.dipasquale.common.random.float1.RandomSupport;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface SearchNodeGroup<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> extends Iterable<TSearchNode> {
    int size();

    boolean isEmpty();

    TSearchNode getByIndex(int index);

    Record<Integer, TSearchNode> getRecordByIndex(int index);

    void swap(int fromIndex, int toIndex);

    int add(TSearchNode searchNode);

    TSearchNode removeByIndex(int index);

    Record<Integer, TSearchNode> removeRecordByIndex(int index);

    TSearchNode removeByKey(int key);

    default void shuffle(final RandomSupport randomSupport) {
        for (int i = size(); i > 1; i--) {
            int fromIndex = i - 1;
            int toIndex = randomSupport.next(0, i);

            swap(fromIndex, toIndex);
        }
    }

    default Stream<TSearchNode> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
