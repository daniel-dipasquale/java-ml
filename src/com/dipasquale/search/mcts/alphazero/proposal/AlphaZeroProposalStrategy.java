package com.dipasquale.search.mcts.alphazero.proposal;

import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.proposal.ActionEfficiencyCalculator;
import com.dipasquale.search.mcts.proposal.ProposalStrategy;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class AlphaZeroProposalStrategy<TAction, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> implements ProposalStrategy<TAction, AlphaZeroEdge, TState, TSearchNode> {
    private final ActionEfficiencyCalculator<AlphaZeroEdge> actionEfficiencyCalculator;
    private final TemperatureController temperatureController;
    private final RankedActionDecisionMaker<TAction, TState, TSearchNode> explorationRankedActionDecisionMaker;
    private final RankedActionDecisionMaker<TAction, TState, TSearchNode> exploitationRankedActionDecisionMaker;

    @Override
    public TSearchNode proposeBestNode(final int simulations, final int depth, final Iterable<TSearchNode> searchNodes) {
        List<RankedAction<TAction, TState, TSearchNode>> rankedActions = new ArrayList<>();

        for (TSearchNode searchNode : searchNodes) {
            float efficiency = actionEfficiencyCalculator.calculate(depth, searchNode.getEdge());

            rankedActions.add(new RankedAction<>(searchNode, efficiency));
        }

        if (temperatureController.shouldExplore(depth)) {
            return explorationRankedActionDecisionMaker.decide(simulations, depth, rankedActions);
        }

        return exploitationRankedActionDecisionMaker.decide(simulations, depth, rankedActions);
    }
}
