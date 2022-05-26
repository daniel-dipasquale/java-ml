package com.dipasquale.search.mcts.propagation;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNodeManager;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.State;

public final class StandardBackPropagationPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TContext> extends AbstractBackPropagationPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>, TContext> {
    public StandardBackPropagationPolicy(final SearchNodeManager<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> searchNodeManager, final BackPropagationStep<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>, TContext> step, final BackPropagationObserver<TAction, TState> observer) {
        super(searchNodeManager, step, observer);
    }
}
