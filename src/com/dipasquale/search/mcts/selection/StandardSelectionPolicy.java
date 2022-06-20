package com.dipasquale.search.mcts.selection;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;

public final class StandardSelectionPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> extends AbstractSelectionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>, Object> {
    public StandardSelectionPolicy(final TraversalPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> unexploredPrimerTraversalPolicy, final TraversalPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> explorableTraversalPolicy, final ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> expansionPolicy) {
        super(unexploredPrimerTraversalPolicy, explorableTraversalPolicy, expansionPolicy);
    }

    @Override
    protected Object createContext() {
        return null;
    }

    @Override
    protected void visit(final Object context, final StandardSearchNode<TAction, TEdge, TState> searchNode) {
    }

    @Override
    protected StandardSearchNode<TAction, TEdge, TState> selectNone() {
        throw new UnsupportedOperationException("unable to select none, this should not happen in a single threaded MCTS implementation");
    }

    @Override
    protected void cleanUp(final Object context) {
    }
}
