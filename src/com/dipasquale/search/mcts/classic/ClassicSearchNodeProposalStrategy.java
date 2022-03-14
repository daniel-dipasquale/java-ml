package com.dipasquale.search.mcts.classic;

import com.dipasquale.common.OptimalPairSelector;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.ActionEfficiencyCalculator;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeProposalStrategy;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ClassicSearchNodeProposalStrategy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements SearchNodeProposalStrategy<TAction, TEdge, TState> {
    private static final Comparator<Float> FLOAT_ASCENDING_COMPARATOR = Float::compare;
    private final ActionEfficiencyCalculator<TAction, TEdge> actionEfficiencyCalculator;

    @Override
    public SearchNode<TAction, TEdge, TState> proposeBestNode(final int simulations, final int depth, final Iterable<SearchNode<TAction, TEdge, TState>> nodes) {
        OptimalPairSelector<Float, SearchNode<TAction, TEdge, TState>> optimalNodeSelector = new OptimalPairSelector<>(FLOAT_ASCENDING_COMPARATOR);

        for (SearchNode<TAction, TEdge, TState> node : nodes) {
            float efficiency = actionEfficiencyCalculator.calculate(depth, node.getAction(), node.getEdge());

            optimalNodeSelector.replaceValueIfBetter(efficiency, node);
        }

        return optimalNodeSelector.getValue();
    }
}
