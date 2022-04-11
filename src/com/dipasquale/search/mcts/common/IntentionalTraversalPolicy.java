package com.dipasquale.search.mcts.common;

import com.dipasquale.common.OptimalPairSelector;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SelectionConfidenceCalculator;
import com.dipasquale.search.mcts.State;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public final class IntentionalTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> extends AbstractExplorableChildrenTraversalPolicy<TAction, TEdge, TState, TSearchNode> {
    private static final Comparator<Float> FLOAT_ASCENDING_COMPARATOR = Float::compare;
    private final SelectionConfidenceCalculator<TEdge> selectionConfidenceCalculator;

    @Override
    protected int nextIndex(final int simulations, final List<TSearchNode> childSearchNodes, final TEdge parentEdge) {
        OptimalPairSelector<Float, Integer> optimalChildIndexSelector = new OptimalPairSelector<>(FLOAT_ASCENDING_COMPARATOR);

        for (int i = 0, c = childSearchNodes.size(); i < c; i++) {
            SearchNode<TAction, TEdge, TState, TSearchNode> childSearchNode = childSearchNodes.get(i);
            float confidence = selectionConfidenceCalculator.calculate(childSearchNode.getEdge(), parentEdge);

            optimalChildIndexSelector.replaceValueIfBetter(confidence, i);
        }

        return optimalChildIndexSelector.getValue();
    }
}
