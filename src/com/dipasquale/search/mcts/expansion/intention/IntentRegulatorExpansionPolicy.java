package com.dipasquale.search.mcts.expansion.intention;

import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class IntentRegulatorExpansionPolicy<TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements ExpansionPolicy<TAction, TEdge, TState, TSearchNode> {
    private final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> intentionalExpansionPolicy;
    private final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> unintentionalExpansionPolicy;

    @Override
    public void expand(final TSearchNode searchNode) {
        if (searchNode.getState().isNextActionIntentional()) {
            intentionalExpansionPolicy.expand(searchNode);
        } else {
            unintentionalExpansionPolicy.expand(searchNode);
        }
    }
}
