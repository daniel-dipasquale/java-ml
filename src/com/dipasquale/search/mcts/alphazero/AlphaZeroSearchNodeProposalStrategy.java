package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeProposalStrategy;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class AlphaZeroSearchNodeProposalStrategy<TAction extends Action, TState extends State<TAction, TState>> implements SearchNodeProposalStrategy<TAction, AlphaZeroEdge, TState> {
    private final RankedActionMapper<TAction, TState> rankedActionMapper;
    private final TemperatureController temperatureController;
    private final RankedActionDecisionMaker<TAction, TState> explorationRankedActionDecisionMaker;
    private final RankedActionDecisionMaker<TAction, TState> exploitationRankedActionDecisionMaker;

    @Override
    public SearchNode<TAction, AlphaZeroEdge, TState> proposeBestNode(final int simulations, final int depth, final Iterable<SearchNode<TAction, AlphaZeroEdge, TState>> nodes) {
        List<RankedAction<TAction, TState>> rankedActions = new ArrayList<>();

        for (RankedAction<TAction, TState> rankedAction : rankedActionMapper.map(simulations, depth, nodes)) {
            rankedActions.add(rankedAction);
        }

        if (temperatureController.shouldExplore(depth)) {
            return explorationRankedActionDecisionMaker.decide(simulations, depth, rankedActions);
        }

        return exploitationRankedActionDecisionMaker.decide(simulations, depth, rankedActions);
    }
}
