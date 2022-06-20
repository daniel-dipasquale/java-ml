package com.dipasquale.search.mcts.expansion;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.State;

public final class StandardExpansionTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> extends AbstractExpansionTraversalPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> {
    public StandardExpansionTraversalPolicy(final ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> expansionPolicy) {
        super(expansionPolicy);
    }
}
