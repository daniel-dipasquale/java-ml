package com.dipasquale.search.mcts.buffer;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;

public interface GenerationTree<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    TSearchNode get(TState state);

    void put(TSearchNode searchNode);

    void clear();
}
