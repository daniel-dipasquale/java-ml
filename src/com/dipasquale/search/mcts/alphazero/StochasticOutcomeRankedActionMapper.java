package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.ActionEfficiencyCalculator;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class StochasticOutcomeRankedActionMapper<TAction extends Action, TState extends State<TAction, TState>> implements RankedActionMapper<TAction, TState> {
    private final ActionEfficiencyCalculator<TAction, AlphaZeroEdge> actionEfficiencyCalculator;

    private Map<Integer, SearchNodeBucket<TAction, TState>> createNodeBuckets(final int depth, final Iterable<SearchNode<TAction, AlphaZeroEdge, TState>> nodes) {
        Map<Integer, SearchNodeBucket<TAction, TState>> nodeBuckets = new HashMap<>();
        SearchNodeBucket<TAction, TState> nodeBucket = null;

        for (SearchNode<TAction, AlphaZeroEdge, TState> node : nodes) {
            TAction action = node.getAction();
            int actionId = action.getId();
            float efficiency = actionEfficiencyCalculator.calculate(depth, action, node.getEdge());

            if (nodeBucket == null || nodeBucket.representative.getAction().getId() != actionId) {
                nodeBucket = new SearchNodeBucket<>(node);
                nodeBuckets.put(actionId, nodeBucket);
            }

            if (Float.compare(efficiency, 0f) > 0) {
                nodeBucket.efficiency += efficiency;
                nodeBucket.count++;
            }
        }

        return nodeBuckets;
    }

    @Override
    public Iterable<RankedAction<TAction, TState>> map(final int simulations, final int depth, final Iterable<SearchNode<TAction, AlphaZeroEdge, TState>> nodes) {
        return createNodeBuckets(depth, nodes).values().stream()
                .map(SearchNodeBucket::createRankedAction)
                ::iterator;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class SearchNodeBucket<TAction extends Action, TState extends State<TAction, TState>> {
        private final SearchNode<TAction, AlphaZeroEdge, TState> representative;
        private float efficiency = 0f;
        private int count = 0;

        RankedAction<TAction, TState> createRankedAction() {
            if (count == 0) {
                return new RankedAction<>(representative, 0f);
            }

            return new RankedAction<>(representative, efficiency / (float) count);
        }
    }
}
