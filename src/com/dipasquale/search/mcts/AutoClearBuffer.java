package com.dipasquale.search.mcts;

import com.dipasquale.common.factory.data.structure.map.MapFactory;

final class AutoClearBuffer<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements Buffer<TAction, TEdge, TState, TSearchNode> {
    private int ownerParticipantId = Integer.MIN_VALUE;
    private final SearchNodeTree<TAction, TEdge, TState, TSearchNode> searchNodes;
    private final EdgeFactory<TEdge> edgeFactory;
    private final SearchNodeFactory<TAction, TEdge, TState, TSearchNode> searchNodeFactory;

    AutoClearBuffer(final MapFactory mapFactory, final EdgeFactory<TEdge> edgeFactory, final SearchNodeFactory<TAction, TEdge, TState, TSearchNode> searchNodeFactory) {
        this.searchNodes = new SearchNodeTree<>(mapFactory);
        this.edgeFactory = edgeFactory;
        this.searchNodeFactory = searchNodeFactory;
    }

    private TSearchNode recallOrCreateRoot(final TState state) {
        if (state.getNextParticipantId() == ownerParticipantId) {
            TSearchNode searchNode = searchNodes.recall(state);

            if (searchNode != null) {
                searchNode.reinitialize(state);

                return searchNode;
            }
        }

        searchNodes.clear();

        return searchNodeFactory.createRoot(edgeFactory, state);
    }

    @Override
    public TSearchNode recallOrCreate(final TState state) {
        TSearchNode searchNode = recallOrCreateRoot(state);

        ownerParticipantId = state.getNextParticipantId();

        return searchNode;
    }

    @Override
    public void store(final TSearchNode searchNode) {
        searchNodes.collect(searchNode);
    }

    @Override
    public void clear() {
        ownerParticipantId = Integer.MIN_VALUE;
        searchNodes.clear();
    }
}
