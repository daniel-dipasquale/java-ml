package com.dipasquale.search.mcts;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public final class SearchNodeCache<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    private final Map<String, SearchNode<TAction, TEdge, TState>> futurePossibleNodes = new HashMap<>();
    private int nextDepth = 0;
    private final int participants;
    private final EdgeFactory<TEdge> edgeFactory;

    private SearchNode<TAction, TEdge, TState> retrieveOrCreateRootNode(final TState state) {
        if (state.getDepth() == nextDepth) {
            SearchNode<TAction, TEdge, TState> node = futurePossibleNodes.get(state.getLastAction().getCacheId());

            if (node != null) {
                node.reinitialize(state);

                return node;
            }
        }

        return SearchNode.createRoot(edgeFactory, state);
    }

    public SearchNode<TAction, TEdge, TState> retrieve(final TState state) {
        SearchNode<TAction, TEdge, TState> node = retrieveOrCreateRootNode(state);

        futurePossibleNodes.clear();
        nextDepth = state.getDepth() + participants;

        return node;
    }

    public boolean storeIfApplicable(final SearchNode<TAction, TEdge, TState> node) {
        if (node.getState().getDepth() + 1 != nextDepth) {
            return false;
        }

        for (SearchNode<TAction, TEdge, TState> childNode : node.getUnexploredChildren()) {
            futurePossibleNodes.put(childNode.getAction().getCacheId(), childNode);
        }

        for (SearchNode<TAction, TEdge, TState> childNode : node.getExplorableChildren()) {
            futurePossibleNodes.put(childNode.getAction().getCacheId(), childNode);
        }

        for (SearchNode<TAction, TEdge, TState> childNode : node.getFullyExploredChildren()) {
            futurePossibleNodes.put(childNode.getAction().getCacheId(), childNode);
        }

        return true;
    }

    public void clear() {
        futurePossibleNodes.clear();
        nextDepth = 0;
    }
}
