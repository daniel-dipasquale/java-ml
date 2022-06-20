package com.dipasquale.search.mcts.propagation;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;

@FunctionalInterface
public interface BackPropagationPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    void process(TSearchNode rootSearchNode, TSearchNode selectedSearchNode, TSearchNode leafSearchNode);
}
