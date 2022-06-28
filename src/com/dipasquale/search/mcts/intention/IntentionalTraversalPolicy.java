package com.dipasquale.search.mcts.intention;

import com.dipasquale.common.OptimalPairScouter;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeGroup;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.heuristic.selection.UctAlgorithm;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;

@RequiredArgsConstructor
public final class IntentionalTraversalPolicy<TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> extends AbstractExplorableChildrenTraversalPolicy<TAction, TEdge, TState, TSearchNode> {
    private static final Comparator<Float> FLOAT_ASCENDING_COMPARATOR = Float::compare;
    private final UctAlgorithm<TEdge> uctAlgorithm;

    @Override
    protected int nextIndex(final int simulations, final TSearchNode parentSearchNode, final SearchNodeGroup<TAction, TEdge, TState, TSearchNode> childSearchNodes) {
        OptimalPairScouter<Float, Integer> optimalIndexScouter = new OptimalPairScouter<>(FLOAT_ASCENDING_COMPARATOR);
        TEdge parentEdge = parentSearchNode.getEdge();

        for (int i = 0, c = childSearchNodes.size(); i < c; i++) {
            TSearchNode childSearchNode = childSearchNodes.getByIndex(i);
            float confidence = uctAlgorithm.calculate(childSearchNode.getEdge(), parentEdge);

            optimalIndexScouter.replaceIfHigherRanking(confidence, i);
        }

        return optimalIndexScouter.getValue();
    }

    @Override
    protected void expand(final TSearchNode childSearchNode) {
        assert childSearchNode.isExpanded();
    }
}
