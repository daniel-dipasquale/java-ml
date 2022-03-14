package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.MaximumConfidenceTraversalPolicy;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SelectionConfidenceCalculator;
import com.dipasquale.search.mcts.SelectionPolicy;
import com.dipasquale.search.mcts.State;

final class AlphaZeroSelectionPolicy<TAction extends Action, TState extends State<TAction, TState>> implements SelectionPolicy<TAction, AlphaZeroEdge, TState> {
    private final AlphaZeroChildrenInitializerTraversalPolicy<TAction, TState> childrenInitializerTraversalPolicy;
    private final MaximumConfidenceTraversalPolicy<TAction, AlphaZeroEdge, TState> maximumConfidenceTraversalPolicy;

    AlphaZeroSelectionPolicy(final AlphaZeroChildrenInitializerTraversalPolicy<TAction, TState> childrenInitializerTraversalPolicy, final SelectionConfidenceCalculator<AlphaZeroEdge> selectionConfidenceCalculator) {
        this.childrenInitializerTraversalPolicy = childrenInitializerTraversalPolicy;
        this.maximumConfidenceTraversalPolicy = new MaximumConfidenceTraversalPolicy<>(selectionConfidenceCalculator);
    }

    @Override
    public SearchNode<TAction, AlphaZeroEdge, TState> select(final int simulations, final SearchNode<TAction, AlphaZeroEdge, TState> rootNode) {
        for (SearchNode<TAction, AlphaZeroEdge, TState> nextNode = rootNode, temporaryNode = maximumConfidenceTraversalPolicy.next(simulations, nextNode); true; temporaryNode = maximumConfidenceTraversalPolicy.next(simulations, nextNode)) {
            if (temporaryNode == null) {
                if (nextNode == rootNode && nextNode.getEdge().getVisited() > 0) {
                    return null;
                }

                childrenInitializerTraversalPolicy.next(simulations, nextNode);

                return nextNode;
            }

            nextNode = temporaryNode;
        }
    }
}
