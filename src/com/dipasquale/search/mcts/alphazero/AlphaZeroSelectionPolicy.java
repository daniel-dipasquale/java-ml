package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.core.Action;
import com.dipasquale.search.mcts.core.ConfidenceCalculator;
import com.dipasquale.search.mcts.core.HighestConfidenceTraversalPolicy;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.State;
import com.dipasquale.search.mcts.core.TraversalPolicy;

public final class AlphaZeroSelectionPolicy<TAction extends Action, TState extends State<TAction, TState>> implements TraversalPolicy<TAction, AlphaZeroEdge, TState> {
    private final AlphaZeroChildrenInitializerTraversalPolicy<TAction, TState> childrenInitializerTraversalPolicy;
    private final HighestConfidenceTraversalPolicy<TAction, AlphaZeroEdge, TState> highestConfidenceTraversalPolicy;

    public AlphaZeroSelectionPolicy(final AlphaZeroChildrenInitializerTraversalPolicy<TAction, TState> childrenInitializerTraversalPolicy, final ConfidenceCalculator<AlphaZeroEdge> confidenceCalculator) {
        this.childrenInitializerTraversalPolicy = childrenInitializerTraversalPolicy;
        this.highestConfidenceTraversalPolicy = new HighestConfidenceTraversalPolicy<>(confidenceCalculator);
    }

    @Override
    public SearchNode<TAction, AlphaZeroEdge, TState> next(final int simulations, final SearchNode<TAction, AlphaZeroEdge, TState> node) {
        for (SearchNode<TAction, AlphaZeroEdge, TState> nextNode = node, temporaryNode = highestConfidenceTraversalPolicy.next(simulations, nextNode); true; temporaryNode = highestConfidenceTraversalPolicy.next(simulations, nextNode)) {
            if (temporaryNode == null) {
                childrenInitializerTraversalPolicy.next(simulations, nextNode);

                return nextNode;
            }

            nextNode = temporaryNode;
        }
    }
}
