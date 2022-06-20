package com.dipasquale.search.mcts.selection;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeGroup;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractUnexploredPrimerTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements TraversalPolicy<TAction, TEdge, TState, TSearchNode> {
    protected TSearchNode selectUnexplored(final TSearchNode searchNode) {
        SearchNodeGroup<TAction, TEdge, TState, TSearchNode> explorableChildren = searchNode.getExplorableChildren();
        SearchNodeGroup<TAction, TEdge, TState, TSearchNode> unexploredChildren = searchNode.getUnexploredChildren();
        TSearchNode childSearchNode = unexploredChildren.removeByIndex(unexploredChildren.size() - 1);
        int explorableChildKey = explorableChildren.add(childSearchNode);

        searchNode.setSelectedExplorableChildKey(explorableChildKey);

        return childSearchNode;
    }

    @Override
    public TSearchNode next(final int simulations, final TSearchNode searchNode) {
        if (searchNode.getUnexploredChildren().isEmpty()) {
            return null;
        }

        return selectUnexplored(searchNode);
    }
}
