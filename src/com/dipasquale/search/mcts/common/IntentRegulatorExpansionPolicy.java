package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class IntentRegulatorExpansionPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements ExpansionPolicy<TAction, TEdge, TState, TSearchNode> {
    private final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> intentionalExpansionPolicy;
    private final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> unintentionalExpansionPolicy;

    @Override
    public void expand(final TSearchNode searchNode) {
        if (searchNode.getState().isNextIntentional()) {
            intentionalExpansionPolicy.expand(searchNode);
        } else {
            unintentionalExpansionPolicy.expand(searchNode);
        }
    }
}
