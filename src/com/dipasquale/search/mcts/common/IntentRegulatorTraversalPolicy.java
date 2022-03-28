package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class IntentRegulatorTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements TraversalPolicy<TAction, TEdge, TState> {
    private final TraversalPolicy<TAction, TEdge, TState> intentionalTraversalPolicy;
    private final TraversalPolicy<TAction, TEdge, TState> unintentionalTraversalPolicy;

    @Override
    public SearchNode<TAction, TEdge, TState> next(final int simulations, final SearchNode<TAction, TEdge, TState> searchNode) {
        if (searchNode.getState().isNextIntentional()) {
            return intentionalTraversalPolicy.next(simulations, searchNode);
        }

        return unintentionalTraversalPolicy.next(simulations, searchNode);
    }
}
