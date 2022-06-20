package com.dipasquale.search.mcts.proposal;

import com.dipasquale.common.OptimalPairScouter;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
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
        OptimalPairScouter<Float, TSearchNode> optimalSearchNodeScouter = new OptimalPairScouter<>(FLOAT_ASCENDING_COMPARATOR);

        for (TSearchNode searchNode : searchNodes) {
            float efficiency = actionEfficiencyCalculator.calculate(depth, searchNode.getEdge());

            optimalSearchNodeScouter.replaceIfHigherRanking(efficiency, searchNode);
        }

        return optimalSearchNodeScouter.getValue();
    }
}
