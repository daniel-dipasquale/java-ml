package com.dipasquale.search.mcts;

import com.dipasquale.common.EntryOptimizer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class HighestConfidenceSelectionPolicy<T extends State> implements SelectionPolicy<T> {
    private static final Comparator<Float> FLOAT_COMPARATOR = Float::compare;
    private final ConfidenceCalculator<T> confidenceCalculator;

    @Override
    public SearchNode<T> next(final int simulations, final SearchNode<T> searchNode) {
        List<SearchNode<T>> childSearchNodes = searchNode.getExplorableChildren();

        if (childSearchNodes.isEmpty()) {
            return null;
        }

        EntryOptimizer<Float, Integer> childSearchNodeOptimizer = new EntryOptimizer<>(FLOAT_COMPARATOR);

        for (int i = 0, c = childSearchNodes.size(); i < c; i++) {
            float confidence = confidenceCalculator.calculate(simulations, childSearchNodes.get(i));

            childSearchNodeOptimizer.collectIfMoreOptimum(confidence, i);
        }

        int index = childSearchNodeOptimizer.getValue();
        SearchNode<T> childSearchNode = childSearchNodes.get(index);

        if (childSearchNode.getEnvironment() == null) {
            childSearchNode.initializeEnvironment();
        }

        searchNode.setChildSelectionIndex(index);

        return childSearchNode;
    }
}
