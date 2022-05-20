package com.dipasquale.search.mcts.common;

import com.dipasquale.common.random.ProbabilityClassifier;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeGroup;
import com.dipasquale.search.mcts.State;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class UnintentionalTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> extends AbstractExplorableChildrenTraversalPolicy<TAction, TEdge, TState, TSearchNode> {
    private final RandomSupport randomSupport;

    @Override
    protected int nextIndex(final int simulations, final SearchNodeGroup<TAction, TEdge, TState, TSearchNode> childSearchNodes, final TSearchNode parentSearchNode) {
        ProbabilityClassifier<Integer> childSearchNodeIndexClassifier = new ProbabilityClassifier<>();

        for (int i = 0, c = childSearchNodes.size(); i < c; i++) {
            TSearchNode childSearchNode = childSearchNodes.getByIndex(i);

            childSearchNodeIndexClassifier.add(childSearchNode.getEdge().getExplorationProbability(), i);
        }

        return childSearchNodeIndexClassifier.get(randomSupport.next());
    }
}
