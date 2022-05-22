package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SelectionConfidenceCalculator;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.State;

public final class StandardIntentionalSelectionTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> extends AbstractIntentionalSelectionTraversalPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>, Object> {
    public StandardIntentionalSelectionTraversalPolicy(final SelectionConfidenceCalculator<TEdge> selectionConfidenceCalculator) {
        super(selectionConfidenceCalculator);
    }
}
