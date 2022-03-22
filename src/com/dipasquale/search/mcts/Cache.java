package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class Cache<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    private int ownerParticipantId = Integer.MIN_VALUE;
    private final SearchNodeGroup<TAction, TEdge, TState> searchNodes = new SearchNodeGroup<>();
    private final EdgeFactory<TEdge> edgeFactory;

    private SearchNode<TAction, TEdge, TState> retrieveOrCreateRoot(final TState state) {
        if (state.getParticipantId() == ownerParticipantId) {
            SearchNode<TAction, TEdge, TState> searchNode = searchNodes.removeDepth(state);

            if (searchNode != null) {
                searchNode.reinitialize(state);

                return searchNode;
            }
        }

        searchNodes.clear();

        return SearchNode.createRoot(edgeFactory, state);
    }

    public SearchNode<TAction, TEdge, TState> retrieve(final TState state) {
        SearchNode<TAction, TEdge, TState> searchNode = retrieveOrCreateRoot(state);

        ownerParticipantId = state.getNextParticipantId();

        return searchNode;
    }

    public boolean storeIfApplicable(final SearchNode<TAction, TEdge, TState> node) {
        if (node.getState().getNextParticipantId() != ownerParticipantId) {
            return false;
        }

        searchNodes.addChildren(node);

        return true;
    }

    public void clear() {
        searchNodes.clear();
        ownerParticipantId = Integer.MIN_VALUE;
    }
}
