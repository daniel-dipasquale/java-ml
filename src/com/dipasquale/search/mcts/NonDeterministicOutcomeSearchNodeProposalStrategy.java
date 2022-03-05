package com.dipasquale.search.mcts;

import com.dipasquale.common.OptimalPairSelector;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NonDeterministicOutcomeSearchNodeProposalStrategy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements SearchNodeProposalStrategy<TAction, TEdge, TState> {
    private static final Comparator<Float> FLOAT_ASCENDING_COMPARATOR = Float::compare;
    private final ActionEfficiencyCalculator<TAction, TEdge> actionEfficiencyCalculator;

    @Override
    public SearchNode<TAction, TEdge, TState> proposeBestNode(final int simulations, final Iterable<SearchNode<TAction, TEdge, TState>> nodes) {
        Map<Integer, SearchNodeBucket<TAction, TEdge, TState>> nodeBuckets = new HashMap<>();

        for (SearchNode<TAction, TEdge, TState> node : nodes) {
            int actionId = node.getAction().getId();
            float efficiency = actionEfficiencyCalculator.calculate(node.getDepth(), node.getAction(), node.getEdge());
            SearchNodeBucket<TAction, TEdge, TState> nodeBucket = nodeBuckets.computeIfAbsent(actionId, __ -> new SearchNodeBucket<>(node));

            nodeBucket.efficiency += efficiency;
        }

        OptimalPairSelector<Float, SearchNode<TAction, TEdge, TState>> optimalNodeSelector = new OptimalPairSelector<>(FLOAT_ASCENDING_COMPARATOR);

        for (SearchNodeBucket<TAction, TEdge, TState> nodeBucket : nodeBuckets.values()) {
            optimalNodeSelector.replaceValueIfBetter(nodeBucket.efficiency, nodeBucket.representative);
        }

        return optimalNodeSelector.getValue();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class SearchNodeBucket<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
        private final SearchNode<TAction, TEdge, TState> representative;
        private float efficiency = 0f;
    }
}
