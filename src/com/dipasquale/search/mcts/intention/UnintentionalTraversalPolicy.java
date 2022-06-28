package com.dipasquale.search.mcts.intention;

import com.dipasquale.common.random.ProbabilityClassifier;
import com.dipasquale.common.random.RandomSupport;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeGroup;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class UnintentionalTraversalPolicy<TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> extends AbstractExplorableChildrenTraversalPolicy<TAction, TEdge, TState, TSearchNode> {
    private final RandomSupport randomSupport;
    private final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy;

    @Override
    protected int nextIndex(final int simulations, final TSearchNode parentSearchNode, final SearchNodeGroup<TAction, TEdge, TState, TSearchNode> childSearchNodes) {
        ProbabilityClassifier<Integer> indexClassifier = new ProbabilityClassifier<>();

        for (int i = 0, c = childSearchNodes.size(); i < c; i++) {
            TSearchNode childSearchNode = childSearchNodes.getByIndex(i);

            indexClassifier.add(childSearchNode.getEdge().getExplorationProbability(), i);
        }

        return indexClassifier.get(randomSupport.nextFloat());
    }

    @Override
    protected void expand(final TSearchNode childSearchNode) {
        expansionPolicy.expand(childSearchNode);
    }
}
