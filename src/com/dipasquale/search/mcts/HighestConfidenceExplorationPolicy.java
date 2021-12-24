package com.dipasquale.search.mcts;

import com.dipasquale.common.EntryOptimizer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class HighestConfidenceExplorationPolicy<T extends State> implements ExplorationPolicy<T> {
    private static final Comparator<Float> FLOAT_COMPARATOR = Float::compare;
    private final SelectionPolicy<T> selectionPolicy;

    @Override
    public SearchNode<T> next(final int simulations, final SearchNode<T> searchNode) {
        List<SearchNode<T>> childSearchNodes = searchNode.getExploredChildren();

        if (childSearchNodes.isEmpty()) {
            return null;
        }

        EntryOptimizer<Float, SearchNode<T>> childSearchNodeOptimizer = new EntryOptimizer<>(FLOAT_COMPARATOR);

        for (SearchNode<T> childSearchNode : childSearchNodes) {
            float confidence = selectionPolicy.calculateConfidence(simulations, childSearchNode);

            childSearchNodeOptimizer.collectIfMoreOptimum(confidence, childSearchNode);
        }

        SearchNode<T> childSearchNode = childSearchNodeOptimizer.getValue();

        if (childSearchNode.getEnvironment() == null) {
            childSearchNode.initializeEnvironment();
        }

        return childSearchNode;
    }
}
