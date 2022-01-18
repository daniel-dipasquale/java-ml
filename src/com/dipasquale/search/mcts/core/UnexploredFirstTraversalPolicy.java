package com.dipasquale.search.mcts.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UnexploredFirstTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements TraversalPolicy<TAction, TEdge, TState> {
    private static final UnexploredFirstTraversalPolicy<?, ?, ?> INSTANCE = new UnexploredFirstTraversalPolicy<>();

    public static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> UnexploredFirstTraversalPolicy<TAction, TEdge, TState> getInstance() {
        return (UnexploredFirstTraversalPolicy<TAction, TEdge, TState>) INSTANCE;
    }

    @Override
    public SearchNode<TAction, TEdge, TState> next(final int simulations, final SearchNode<TAction, TEdge, TState> node) {
        List<SearchNode<TAction, TEdge, TState>> childNodes = node.getUnexploredChildren();
        int size = childNodes.size();

        if (size == 0) {
            return null;
        }

        SearchNode<TAction, TEdge, TState> childNode = childNodes.remove(size - 1);

        node.getExplorableChildren().add(childNode);
        childNode.initializeState();
        node.setSelectedExplorableChildIndex(node.getExplorableChildren().size() - 1);

        return childNode;
    }
}
