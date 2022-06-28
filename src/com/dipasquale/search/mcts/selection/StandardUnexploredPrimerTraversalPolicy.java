package com.dipasquale.search.mcts.selection;

import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;

public final class StandardUnexploredPrimerTraversalPolicy<TAction, TEdge extends Edge, TState extends State<TAction, TState>> extends AbstractUnexploredPrimerTraversalPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> {
    public StandardUnexploredPrimerTraversalPolicy(final ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> expansionPolicy) {
        super(expansionPolicy);
    }
}
