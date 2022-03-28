package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class IntentRegulatorExpansionPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements ExpansionPolicy<TAction, TEdge, TState> {
    private final ExpansionPolicy<TAction, TEdge, TState> intentionalExpansionPolicy;
    private final ExpansionPolicy<TAction, TEdge, TState> unintentionalExpansionPolicy;

    @Override
    public void expand(final SearchNode<TAction, TEdge, TState> searchNode) {
        if (searchNode.getState().isNextIntentional()) {
            intentionalExpansionPolicy.expand(searchNode);
        } else {
            unintentionalExpansionPolicy.expand(searchNode);
        }
    }
}
