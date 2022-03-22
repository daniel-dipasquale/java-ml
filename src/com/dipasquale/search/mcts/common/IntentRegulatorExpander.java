package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.Expander;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class IntentRegulatorExpander<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements Expander<TAction, TEdge, TState> {
    private final Expander<TAction, TEdge, TState> intentionalExpander;
    private final Expander<TAction, TEdge, TState> unintentionalExpander;

    @Override
    public void expand(final SearchNode<TAction, TEdge, TState> searchNode) {
        if (searchNode.getState().isNextIntentional()) {
            intentionalExpander.expand(searchNode);
        } else {
            unintentionalExpander.expand(searchNode);
        }
    }
}
