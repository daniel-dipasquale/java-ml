package com.dipasquale.search.mcts.core;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class UnexploredFirstSelectionPolicy<TState extends SearchState, TEdge extends SearchEdge> implements SelectionPolicy<TState, TEdge> {
    @Override
    public SearchNode<TState, TEdge> next(final int simulations, final SearchNode<TState, TEdge> node) {
        List<SearchNode<TState, TEdge>> childNodes = node.getUnexploredChildren();
        int size = childNodes.size();

        if (size == 0) {
            return null;
        }

        SearchNode<TState, TEdge> childNode = childNodes.remove(size - 1);

        node.getExplorableChildren().add(childNode);
        childNode.initializeEnvironment();
        node.setChildSelectedIndex(node.getExplorableChildren().size() - 1);

        return childNode;
    }
}
