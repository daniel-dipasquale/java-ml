package com.dipasquale.search.mcts.common;

import com.dipasquale.common.OptimalPairSelector;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.ActionEfficiencyCalculator;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.ProposalStrategy;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;

@RequiredArgsConstructor
public final class MaximumEfficiencyProposalStrategy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements ProposalStrategy<TAction, TEdge, TState, TSearchNode> {
    private static final Comparator<Float> FLOAT_ASCENDING_COMPARATOR = Float::compare;
    private final ActionEfficiencyCalculator<TEdge> actionEfficiencyCalculator;

    @Override
    public TSearchNode proposeBestNode(final int simulations, final int depth, final Iterable<TSearchNode> searchNodes) {
        OptimalPairSelector<Float, TSearchNode> optimalSearchNodeSelector = new OptimalPairSelector<>(FLOAT_ASCENDING_COMPARATOR);

        for (TSearchNode searchNode : searchNodes) {
            float efficiency = actionEfficiencyCalculator.calculate(depth, searchNode.getEdge());

            optimalSearchNodeSelector.replaceValueIfBetter(efficiency, searchNode);
        }

        return optimalSearchNodeSelector.getValue();
    }
}
