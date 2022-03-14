package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.ActionEfficiencyCalculator;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.stream.StreamSupport;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class DeterministicOutcomeRankedActionMapper<TAction extends Action, TState extends State<TAction, TState>> implements RankedActionMapper<TAction, TState> {
    private final ActionEfficiencyCalculator<TAction, AlphaZeroEdge> actionEfficiencyCalculator;

    private RankedAction<TAction, TState> createRankedAction(final int depth, final SearchNode<TAction, AlphaZeroEdge, TState> node) {
        float efficiency = actionEfficiencyCalculator.calculate(depth, node.getAction(), node.getEdge());

        return new RankedAction<>(node, efficiency);
    }

    @Override
    public Iterable<RankedAction<TAction, TState>> map(final int simulations, final int depth, final Iterable<SearchNode<TAction, AlphaZeroEdge, TState>> nodes) {
        return StreamSupport.stream(nodes.spliterator(), false)
                .map(node -> createRankedAction(depth, node))
                ::iterator;
    }
}
