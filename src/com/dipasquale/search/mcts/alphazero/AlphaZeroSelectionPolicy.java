package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.MaximumConfidenceTraversalPolicy;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SelectionConfidenceCalculator;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;

public final class AlphaZeroSelectionPolicy<TAction extends Action, TState extends State<TAction, TState>> implements TraversalPolicy<TAction, AlphaZeroEdge, TState> {
    private final AlphaZeroChildrenInitializerTraversalPolicy<TAction, TState> childrenInitializerTraversalPolicy;
    private final MaximumConfidenceTraversalPolicy<TAction, AlphaZeroEdge, TState> maximumConfidenceTraversalPolicy;

    public AlphaZeroSelectionPolicy(final AlphaZeroChildrenInitializerTraversalPolicy<TAction, TState> childrenInitializerTraversalPolicy, final SelectionConfidenceCalculator<AlphaZeroEdge> selectionConfidenceCalculator) {
        this.childrenInitializerTraversalPolicy = childrenInitializerTraversalPolicy;
        this.maximumConfidenceTraversalPolicy = new MaximumConfidenceTraversalPolicy<>(selectionConfidenceCalculator);
    }

    @Override
    public SearchNode<TAction, AlphaZeroEdge, TState> next(final int simulations, final SearchNode<TAction, AlphaZeroEdge, TState> node) {
        for (SearchNode<TAction, AlphaZeroEdge, TState> nextNode = node, temporaryNode = maximumConfidenceTraversalPolicy.next(simulations, nextNode); true; temporaryNode = maximumConfidenceTraversalPolicy.next(simulations, nextNode)) {
            if (temporaryNode == null) {
                childrenInitializerTraversalPolicy.next(simulations, nextNode);

                return nextNode;
            }

            nextNode = temporaryNode;
        }
    }
}
