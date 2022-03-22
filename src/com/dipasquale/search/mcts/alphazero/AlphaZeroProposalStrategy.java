package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.ActionEfficiencyCalculator;
import com.dipasquale.search.mcts.ProposalStrategy;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class AlphaZeroProposalStrategy<TAction extends Action, TState extends State<TAction, TState>> implements ProposalStrategy<TAction, AlphaZeroEdge, TState> {
    private final ActionEfficiencyCalculator<AlphaZeroEdge> actionEfficiencyCalculator;
    private final TemperatureController temperatureController;
    private final RankedActionDecisionMaker<TAction, TState> explorationRankedActionDecisionMaker;
    private final RankedActionDecisionMaker<TAction, TState> exploitationRankedActionDecisionMaker;

    @Override
    public SearchNode<TAction, AlphaZeroEdge, TState> proposeBestNode(final int simulations, final int depth, final Iterable<SearchNode<TAction, AlphaZeroEdge, TState>> searchNodes) {
        List<RankedAction<TAction, TState>> rankedActions = new ArrayList<>();

        for (SearchNode<TAction, AlphaZeroEdge, TState> searchNode : searchNodes) {
            float efficiency = actionEfficiencyCalculator.calculate(depth, searchNode.getEdge());

            rankedActions.add(new RankedAction<>(searchNode, efficiency));
        }

        if (temperatureController.shouldExplore(depth)) {
            return explorationRankedActionDecisionMaker.decide(simulations, depth, rankedActions);
        }

        return exploitationRankedActionDecisionMaker.decide(simulations, depth, rankedActions);
    }
}
