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
    protected TSearchNode selectUnexplored(final TSearchNode currentSearchNode) {
        SearchNodeGroup<TAction, TEdge, TState, TSearchNode> explorableChildren = currentSearchNode.getExplorableChildren();
        SearchNodeGroup<TAction, TEdge, TState, TSearchNode> unexploredChildren = currentSearchNode.getUnexploredChildren();
        TSearchNode childSearchNode = unexploredChildren.removeByIndex(unexploredChildren.size() - 1);
        int explorableChildKey = explorableChildren.add(childSearchNode);

        currentSearchNode.setSelectedExplorableChildKey(explorableChildKey);

        return childSearchNode;
    }

    @Override
    public TSearchNode next(final int simulations, final TSearchNode currentSearchNode) {
        if (currentSearchNode.getUnexploredChildren().isEmpty()) {
            return null;
        }

        return selectUnexplored(currentSearchNode);
    }
}
