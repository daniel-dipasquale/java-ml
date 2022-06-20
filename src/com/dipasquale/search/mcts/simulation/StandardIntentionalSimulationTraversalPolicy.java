package com.dipasquale.search.mcts.simulation;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;

public final class StandardIntentionalSimulationTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> extends AbstractIntentionalSimulationTraversalPolicy<TAction, TEdge, TState, TSearchNode> {
    public StandardIntentionalSimulationTraversalPolicy(final RandomSupport randomSupport) {
        super(randomSupport);
    }
}
