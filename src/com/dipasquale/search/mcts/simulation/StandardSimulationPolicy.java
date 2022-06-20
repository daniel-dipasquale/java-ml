package com.dipasquale.search.mcts.simulation;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import com.dipasquale.search.mcts.seek.ComprehensiveSeekPolicy;

public final class StandardSimulationPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> extends AbstractSimulationPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>, Object> {
    public StandardSimulationPolicy(final ComprehensiveSeekPolicy comprehensiveSeekPolicy, final TraversalPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> traversalPolicy, final ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> expansionPolicy) {
        super(comprehensiveSeekPolicy, traversalPolicy, expansionPolicy);
    }

    @Override
    protected Object createContext() {
        return null;
    }

    @Override
    protected void visit(final Object context, final StandardSearchNode<TAction, TEdge, TState> searchNode) {
    }

    @Override
    protected void cleanUp(final Object context) {
    }
}
