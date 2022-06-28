package com.dipasquale.search.mcts.intention;

import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class IntentRegulatorTraversalPolicy<TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements TraversalPolicy<TAction, TEdge, TState, TSearchNode> {
    private final TraversalPolicy<TAction, TEdge, TState, TSearchNode> intentionalTraversalPolicy;
    private final TraversalPolicy<TAction, TEdge, TState, TSearchNode> unintentionalTraversalPolicy;

    @Override
    public TSearchNode next(final int simulations, final TSearchNode searchNode) {
        if (searchNode.getState().isNextActionIntentional()) {
            return intentionalTraversalPolicy.next(simulations, searchNode);
        }

        return unintentionalTraversalPolicy.next(simulations, searchNode);
    }
}
