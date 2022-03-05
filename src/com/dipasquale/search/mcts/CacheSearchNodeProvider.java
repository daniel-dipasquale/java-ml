package com.dipasquale.search.mcts;

import lombok.RequiredArgsConstructor;

import java.util.IdentityHashMap;
import java.util.Map;

@RequiredArgsConstructor
public final class CacheSearchNodeProvider<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements SearchNodeProvider<TAction, TEdge, TState> {
    private final Map<State<TAction, TState>, SearchNode<TAction, TEdge, TState>> futurePossibleNodes = new IdentityHashMap<>();
    private int nextDepth = 0;
    private final int participants;
    private final EdgeFactory<TEdge> edgeFactory;

    @Override
    public SearchNode<TAction, TEdge, TState> provide(final TState state) {
        SearchNode<TAction, TEdge, TState> node = futurePossibleNodes.get(state);

        if (node != null) {
            node.removeParent();
            nextDepth = node.getDepth() + participants;
            futurePossibleNodes.clear();

            return node;
        }

        node = SearchNodeProvider.createRootNode(edgeFactory, state, nextDepth);
        nextDepth += participants;
        futurePossibleNodes.clear();

        return node;
    }

    @Override
    public boolean registerIfApplicable(final SearchNode<TAction, TEdge, TState> node) {
        if (node.getDepth() + 1 != nextDepth) {
            return false;
        }

        for (SearchNode<TAction, TEdge, TState> childNode : node.getUnexploredChildren()) {
            futurePossibleNodes.put(childNode.getState(), childNode);
        }

        for (SearchNode<TAction, TEdge, TState> childNode : node.getExplorableChildren()) {
            futurePossibleNodes.put(childNode.getState(), childNode);
        }

        for (SearchNode<TAction, TEdge, TState> childNode : node.getFullyExploredChildren()) {
            futurePossibleNodes.put(childNode.getState(), childNode);
        }

        return true;
    }
}
