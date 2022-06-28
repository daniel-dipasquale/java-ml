package com.dipasquale.search.mcts.expansion;

import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.State;

public final class StandardOptionalExpansionPolicy<TAction, TEdge extends Edge, TState extends State<TAction, TState>> extends AbstractOptionalExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> {
    public StandardOptionalExpansionPolicy(final ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> expansionPolicy) {
        super(expansionPolicy);
    }
}
