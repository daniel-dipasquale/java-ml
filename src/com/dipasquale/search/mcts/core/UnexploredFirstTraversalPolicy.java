package com.dipasquale.search.mcts.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UnexploredFirstTraversalPolicy<TState extends State, TEdge extends Edge, TEnvironment extends Environment<TState, TEnvironment>> implements TraversalPolicy<TState, TEdge, TEnvironment> {
    private static final UnexploredFirstTraversalPolicy<?, ?, ?> INSTANCE = new UnexploredFirstTraversalPolicy<>();

    public static <TState extends State, TEdge extends Edge, TEnvironment extends Environment<TState, TEnvironment>> UnexploredFirstTraversalPolicy<TState, TEdge, TEnvironment> getInstance() {
        return (UnexploredFirstTraversalPolicy<TState, TEdge, TEnvironment>) INSTANCE;
    }

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
        node.setSelectedExplorableChildIndex(node.getExplorableChildren().size() - 1);

        return childNode;
    }
}
