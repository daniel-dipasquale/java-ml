package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractSearchNodeManager<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements SearchNodeManager<TAction, TEdge, TState, TSearchNode> {
    @Override
    public boolean isFullyExplored(final TSearchNode searchNode) {
        return searchNode.isFullyExplored();
    }

    @Override
    public boolean declareFullyExplored(final TSearchNode searchNode) {
        TSearchNode parentSearchNode = searchNode.getParent();

        if (parentSearchNode.getExplorableChildren().removeByKey(parentSearchNode.getSelectedExplorableChildKey()) == searchNode) {
            parentSearchNode.getFullyExploredChildren().add(searchNode);
        }

        return parentSearchNode.isFullyExplored();
    }
}
