package com.dipasquale.search.mcts.core;

import com.dipasquale.common.EntryOptimizer;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public final class HighestConfidenceTraversalPolicy<TState extends State, TEdge extends Edge, TEnvironment extends Environment<TState, TEnvironment>> implements TraversalPolicy<TState, TEdge, TEnvironment> {
    private static final Comparator<Float> FLOAT_COMPARATOR = Float::compare;
    private final ConfidenceCalculator<TEdge> confidenceCalculator;

    @Override
    public SearchNode<TState, TEdge, TEnvironment> next(final int simulations, final SearchNode<TState, TEdge, TEnvironment> node) {
        List<SearchNode<TState, TEdge, TEnvironment>> childNodes = node.getExplorableChildren();
        int size = node.getExplorableChildren().size();

        if (size == 0) {
            return null;
        }

        EntryOptimizer<Float, Integer> childNodeOptimizer = new EntryOptimizer<>(FLOAT_COMPARATOR);

        for (int i = 0; i < size; i++) {
            float confidence = confidenceCalculator.calculate(simulations, childNodes.get(i).getEdge());

            childNodeOptimizer.replaceValueIfMoreOptimum(confidence, i);
        }

        int index = childNodeOptimizer.getValue();
        SearchNode<TState, TEdge, TEnvironment> childNode = childNodes.get(index);

        if (childNode.getEnvironment() == null) {
            childNode.initializeEnvironment();
        }

        node.setSelectedExplorableChildIndex(index);

        return childNode;
    }
}
