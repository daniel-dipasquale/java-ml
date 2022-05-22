package com.dipasquale.search.mcts.common;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;

public final class StandardIntentionalSimulationRolloutTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> extends AbstractIntentionalSimulationRolloutTraversalPolicy<TAction, TEdge, TState, TSearchNode> {
    public StandardIntentionalSimulationRolloutTraversalPolicy(final RandomSupport randomSupport) {
        super(randomSupport);
    }
}
