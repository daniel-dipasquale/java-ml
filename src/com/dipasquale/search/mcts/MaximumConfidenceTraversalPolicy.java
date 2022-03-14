package com.dipasquale.search.mcts;

import com.dipasquale.common.OptimalPairSelector;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public final class MaximumConfidenceTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements TraversalPolicy<TAction, TEdge, TState> {
    private static final Comparator<Float> FLOAT_ASCENDING_COMPARATOR = Float::compare;
    private final SelectionConfidenceCalculator<TEdge> selectionConfidenceCalculator;

    @Override
    public SearchNode<TAction, TEdge, TState> next(final int simulations, final SearchNode<TAction, TEdge, TState> node) {
        if (!node.isExpanded()) {
            return null;
        }

        List<SearchNode<TAction, TEdge, TState>> childNodes = node.getExplorableChildren();
        int size = node.getExplorableChildren().size();

        if (size == 0) {
            return null;
        }

        OptimalPairSelector<Float, Integer> optimalChildNodeSelector = new OptimalPairSelector<>(FLOAT_ASCENDING_COMPARATOR);
        TEdge parentEdge = node.getEdge();

        for (int i = 0; i < size; i++) {
            float confidence = selectionConfidenceCalculator.calculate(childNodes.get(i).getEdge(), parentEdge);

            optimalChildNodeSelector.replaceValueIfBetter(confidence, i);
        }

        int index = optimalChildNodeSelector.getValue();
        SearchNode<TAction, TEdge, TState> childNode = childNodes.get(index);

        if (childNode.getState() == null) {
            childNode.initializeState();
        }

        node.setSelectedExplorableChildIndex(index);

        return childNode;
    }
}
