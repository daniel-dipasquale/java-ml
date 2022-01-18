package com.dipasquale.search.mcts.core;

import com.dipasquale.common.EntryOptimizer;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public final class HighestConfidenceTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements TraversalPolicy<TAction, TEdge, TState> {
    private static final Comparator<Float> FLOAT_COMPARATOR = Float::compare;
    private final ConfidenceCalculator<TEdge> confidenceCalculator;

    @Override
    public SearchNode<TAction, TEdge, TState> next(final int simulations, final SearchNode<TAction, TEdge, TState> node) {
        List<SearchNode<TAction, TEdge, TState>> childNodes = node.getExplorableChildren();
        int size = node.getExplorableChildren().size();

        if (size == 0) {
            return null;
        }

        EntryOptimizer<Float, Integer> childNodeOptimizer = new EntryOptimizer<>(FLOAT_COMPARATOR);

        for (int i = 0; i < size; i++) {
            float confidence = confidenceCalculator.calculate(childNodes.get(i).getEdge());

            childNodeOptimizer.replaceValueIfMoreOptimum(confidence, i);
        }

        int index = childNodeOptimizer.getValue();
        SearchNode<TAction, TEdge, TState> childNode = childNodes.get(index);

        if (childNode.getState() == null) {
            childNode.initializeState();
        }

        node.setSelectedExplorableChildIndex(index);

        return childNode;
    }
}
