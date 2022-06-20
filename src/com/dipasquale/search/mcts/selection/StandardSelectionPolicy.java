package com.dipasquale.search.mcts.selection;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;

public final class StandardSelectionPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> extends AbstractSelectionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>, Object> {
    public StandardSelectionPolicy(final TraversalPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> priorityTraversalPolicy, final TraversalPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> subsequentTraversalPolicy, final ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> expansionPolicy) {
        super(priorityTraversalPolicy, subsequentTraversalPolicy, expansionPolicy);
    }

    @Override
    protected Object createContext() {
        return null;
    }

    @Override
    protected void visit(final Object context, final StandardSearchNode<TAction, TEdge, TState> searchNode) {
    }

    @Override
    protected void exit(final Object context) {
    }
}
