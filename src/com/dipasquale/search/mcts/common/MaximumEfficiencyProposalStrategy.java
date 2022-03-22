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
public final class MaximumEfficiencyProposalStrategy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements ProposalStrategy<TAction, TEdge, TState> {
    private static final Comparator<Float> FLOAT_ASCENDING_COMPARATOR = Float::compare;
    private final ActionEfficiencyCalculator<TEdge> actionEfficiencyCalculator;

    @Override
    public SearchNode<TAction, TEdge, TState> proposeBestNode(final int simulations, final int depth, final Iterable<SearchNode<TAction, TEdge, TState>> searchNodes) {
        OptimalPairSelector<Float, SearchNode<TAction, TEdge, TState>> optimalSearchNodeSelector = new OptimalPairSelector<>(FLOAT_ASCENDING_COMPARATOR);

        for (SearchNode<TAction, TEdge, TState> searchNode : searchNodes) {
            float efficiency = actionEfficiencyCalculator.calculate(depth, searchNode.getEdge());

            optimalSearchNodeSelector.replaceValueIfBetter(efficiency, searchNode);
        }

        return optimalSearchNodeSelector.getValue();
    }
}
