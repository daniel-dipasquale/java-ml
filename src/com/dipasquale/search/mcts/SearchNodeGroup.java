package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SearchNodeGroup<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    private final Map<Integer, Map<String, SearchNode<TAction, TEdge, TState>>> searchNodesByDepth = new HashMap<>();

    public SearchNode<TAction, TEdge, TState> removeDepth(final TState state) {
        Map<String, SearchNode<TAction, TEdge, TState>> searchNodes = searchNodesByDepth.remove(state.getDepth());

        if (searchNodes == null) {
            return null;
        }

        return searchNodes.get(state.getLastAction().getCacheId());
    }

    public void addChildren(final SearchNode<TAction, TEdge, TState> searchNode) {
        int depth = searchNode.getState().getDepth() + 1;
        Map<String, SearchNode<TAction, TEdge, TState>> searchNodes = searchNodesByDepth.computeIfAbsent(depth, __ -> new HashMap<>());

        for (SearchNode<TAction, TEdge, TState> childSearchNode : searchNode.getUnexploredChildren()) {
            searchNodes.put(childSearchNode.getAction().getCacheId(), childSearchNode);
        }

        for (SearchNode<TAction, TEdge, TState> childSearchNode : searchNode.getExplorableChildren()) {
            searchNodes.put(childSearchNode.getAction().getCacheId(), childSearchNode);
        }

        for (SearchNode<TAction, TEdge, TState> childSearchNode : searchNode.getFullyExploredChildren()) {
            searchNodes.put(childSearchNode.getAction().getCacheId(), childSearchNode);
        }
    }

    public void clear() {
        searchNodesByDepth.clear();
    }
}
