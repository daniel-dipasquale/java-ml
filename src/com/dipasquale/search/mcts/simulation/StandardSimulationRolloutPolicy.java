package com.dipasquale.search.mcts.simulation;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import com.dipasquale.search.mcts.seek.FullSeekPolicy;

public final class StandardSimulationRolloutPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> extends AbstractSimulationRolloutPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>, Object> {
    public StandardSimulationRolloutPolicy(final FullSeekPolicy searchPolicy, final TraversalPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> traversalPolicy, final ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> expansionPolicy) {
        super(searchPolicy, traversalPolicy, expansionPolicy);
    }

    @Override
    protected Object createContext() {
        return null;
    }

    @Override
    protected void visit(final Object context, final StandardSearchNode<TAction, TEdge, TState> currentSearchNode) {
    }

    @Override
    protected boolean selected(final Object context, final StandardSearchNode<TAction, TEdge, TState> currentSearchNode) {
        return true;
    }

    @Override
    protected void exit(final Object context) {
    }
}
