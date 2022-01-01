package com.dipasquale.search.mcts.core;

import com.dipasquale.common.EntryOptimizer;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public final class HighestConfidenceSelectionPolicy<TState extends SearchState, TEdge extends SearchEdge> implements SelectionPolicy<TState, TEdge> {
    private static final Comparator<Float> FLOAT_COMPARATOR = Float::compare;
    private final ConfidenceCalculator<TEdge> confidenceCalculator;

    @Override
    public SearchNode<TState, TEdge> next(final int simulations, final SearchNode<TState, TEdge> node) {
        List<SearchNode<TState, TEdge>> childNodes = node.getExplorableChildren();
        int size = node.getExplorableChildren().size();

        if (size == 0) {
            return null;
        }

        EntryOptimizer<Float, Integer> childNodeOptimizer = new EntryOptimizer<>(FLOAT_COMPARATOR);

        for (int i = 0; i < size; i++) {
            float confidence = confidenceCalculator.calculate(simulations, childNodes.get(i).getEdge());

            childNodeOptimizer.collectIfMoreOptimum(confidence, i);
        }

        int index = childNodeOptimizer.getValue();
        SearchNode<TState, TEdge> childNode = childNodes.get(index);

        if (childNode.getEnvironment() == null) {
            childNode.initializeEnvironment();
        }

        node.setChildSelectedIndex(index);

        return childNode;
    }
}
