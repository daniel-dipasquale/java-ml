package com.dipasquale.search.mcts.expansion.intention;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeGroup;
import com.dipasquale.search.mcts.SearchNodeGroupProvider;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class IntentionalExpansionPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements ExpansionPolicy<TAction, TEdge, TState, TSearchNode> {
    private final EdgeFactory<TEdge> edgeFactory;
    private final SearchNodeGroupProvider<TAction, TEdge, TState, TSearchNode> searchNodeGroupProvider;
    private final RandomSupport randomSupport;

    @Override
    public void expand(final TSearchNode searchNode) {
        Iterable<TSearchNode> unexploredChildrenIterable = searchNode.createAllPossibleChildNodes(edgeFactory);
        SearchNodeGroup<TAction, TEdge, TState, TSearchNode> unexploredChildren = searchNodeGroupProvider.create(unexploredChildrenIterable);

        unexploredChildren.shuffle(randomSupport);
        searchNode.setUnexploredChildren(unexploredChildren);
        searchNode.setExplorableChildren(searchNodeGroupProvider.create(null));
        searchNode.setFullyExploredChildren(searchNodeGroupProvider.create(null));
    }
}
