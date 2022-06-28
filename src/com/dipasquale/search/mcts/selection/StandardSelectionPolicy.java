package com.dipasquale.search.mcts.selection;

import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;

public final class StandardSelectionPolicy<TAction, TEdge extends Edge, TState extends State<TAction, TState>> extends AbstractSelectionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>, Object> {
    public StandardSelectionPolicy(final ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> expansionPolicy, final TraversalPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> unexploredPrimerTraversalPolicy, final TraversalPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> explorableTraversalPolicy) {
        super(expansionPolicy, unexploredPrimerTraversalPolicy, explorableTraversalPolicy);
    }

    @Override
    protected Object createContext() {
        return null;
    }

    @Override
    protected void visit(final Object context, final StandardSearchNode<TAction, TEdge, TState> searchNode) {
    }

    @Override
    protected boolean shouldSelectCandidateLeaf(final StandardSearchNode<TAction, TEdge, TState> candidateSearchNode) {
        return true;
    }

    @Override
    protected boolean shouldSelectKnownLeaf(final StandardSearchNode<TAction, TEdge, TState> knownSearchNode) {
        return true;
    }

    @Override
    protected void cleanUp(final Object context) {
    }
}
