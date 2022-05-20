package com.dipasquale.search.mcts.common;

import com.dipasquale.common.OptimalPairSelector;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeGroup;
import com.dipasquale.search.mcts.SelectionConfidenceCalculator;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractIntentionalTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>, TContext> extends AbstractExplorableChildrenTraversalPolicy<TAction, TEdge, TState, TSearchNode> {
    private static final Comparator<Float> FLOAT_ASCENDING_COMPARATOR = Float::compare;
    private final SelectionConfidenceCalculator<TEdge> selectionConfidenceCalculator;

    protected TContext createContext() {
        return null;
    }

    protected void collectConfidenceCalculation(final TContext context, final TSearchNode childSearchNode, final TEdge parentEdge, final OptimalPairSelector<Float, Integer> optimalChildIndexSelector, final int index) {
        float confidence = selectionConfidenceCalculator.calculate(childSearchNode.getEdge(), parentEdge);

        optimalChildIndexSelector.replaceValueIfBetter(confidence, index);
    }

    protected int getOptimalIndex(final TContext context, final TSearchNode parentSearchNode, final OptimalPairSelector<Float, Integer> optimalChildIndexSelector) {
        return optimalChildIndexSelector.getValue();
    }

    @Override
    protected int nextIndex(final int simulations, final SearchNodeGroup<TAction, TEdge, TState, TSearchNode> childSearchNodes, final TSearchNode parentSearchNode) {
        TContext context = createContext();
        TEdge parentEdge = parentSearchNode.getEdge();
        OptimalPairSelector<Float, Integer> optimalChildIndexSelector = new OptimalPairSelector<>(FLOAT_ASCENDING_COMPARATOR);

        for (int i = 0, c = childSearchNodes.size(); i < c; i++) {
            TSearchNode childSearchNode = childSearchNodes.getByIndex(i);

            collectConfidenceCalculation(context, childSearchNode, parentEdge, optimalChildIndexSelector, i);
        }

        return getOptimalIndex(context, parentSearchNode, optimalChildIndexSelector);
    }
}
