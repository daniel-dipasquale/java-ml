package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class AutoClearCacheProvider<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements Provider<TAction, TEdge, TState> {
    private static final boolean IS_ALLOWED_TO_COLLECT = true;
    private int ownerParticipantId = Integer.MIN_VALUE;
    private final SearchNodeTree<TAction, TEdge, TState> searchNodes = new SearchNodeTree<>();
    private final EdgeFactory<TEdge> edgeFactory;

    @Override
    public boolean isAllowedToCollect() {
        return IS_ALLOWED_TO_COLLECT;
    }

    private SearchNode<TAction, TEdge, TState> recallOrCreateRoot(final TState state) {
        if (state.getNextParticipantId() == ownerParticipantId) {
            SearchNode<TAction, TEdge, TState> searchNode = searchNodes.recall(state);

            if (searchNode != null) {
                searchNode.reinitialize(state);

                return searchNode;
            }
        }

        searchNodes.clear();

        return SearchNode.createRoot(edgeFactory, state);
    }

    @Override
    public SearchNode<TAction, TEdge, TState> recallOrCreate(final TState state) {
        SearchNode<TAction, TEdge, TState> searchNode = recallOrCreateRoot(state);

        ownerParticipantId = state.getNextParticipantId();

        return searchNode;
    }

    @Override
    public void collect(final SearchNode<TAction, TEdge, TState> searchNode) {
        searchNodes.collect(searchNode);
    }

    @Override
    public void clear() {
        ownerParticipantId = Integer.MIN_VALUE;
        searchNodes.clear();
    }
}
