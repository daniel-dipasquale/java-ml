package com.dipasquale.search.mcts.core;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class UnexploredFirstSelectionPolicy<TState extends SearchState, TEdge extends SearchEdge, TEnvironment extends Environment<TState, TEnvironment>> implements SelectionPolicy<TState, TEdge, TEnvironment> {
    @Override
    public SearchNode<TState, TEdge, TEnvironment> next(final int simulations, final SearchNode<TState, TEdge, TEnvironment> node) {
        List<SearchNode<TState, TEdge, TEnvironment>> childNodes = node.getUnexploredChildren();
        int size = childNodes.size();

        if (size == 0) {
            return null;
        }

        SearchNode<TState, TEdge, TEnvironment> childNode = childNodes.remove(size - 1);

        node.getExplorableChildren().add(childNode);
        childNode.initializeEnvironment();
        node.setChildSelectedIndex(node.getExplorableChildren().size() - 1);

        return childNode;
    }
}
