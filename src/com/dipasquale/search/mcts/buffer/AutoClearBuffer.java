package com.dipasquale.search.mcts.buffer;

import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeFactory;
import com.dipasquale.search.mcts.SearchResult;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class AutoClearBuffer<TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements Buffer<TAction, TEdge, TState, TSearchNode> {
    private int ownerParticipantId = Integer.MIN_VALUE;
    private final GenerationTree<TAction, TEdge, TState, TSearchNode> generationTree = new GenerationTree<>();
    private final SearchNodeFactory<TAction, TEdge, TState, TSearchNode> searchNodeFactory;

    private TSearchNode getOrCreateRoot(final SearchResult<TAction, TState> searchResult) {
        if (searchResult.getState().getNextParticipantId() == ownerParticipantId) {
            TSearchNode searchNode = generationTree.reseed(searchResult.getStateId());

            if (searchNode == null) {
                searchNode = searchNodeFactory.createRoot(searchResult);
                generationTree.seed(searchNode);
            } else {
                searchNode.reinitialize(searchResult);
            }

            return searchNode;
        }

        TSearchNode searchNode = searchNodeFactory.createRoot(searchResult);

        generationTree.seed(searchNode);

        return searchNode;
    }

    @Override
    public TSearchNode provide(final SearchResult<TAction, TState> searchResult) {
        TSearchNode searchNode = getOrCreateRoot(searchResult);

        ownerParticipantId = searchResult.getState().getNextParticipantId();

        return searchNode;
    }

    @Override
    public void put(final TSearchNode searchNode) {
        generationTree.branchOut(searchNode);
    }

    @Override
    public void clear() {
        ownerParticipantId = Integer.MIN_VALUE;
        generationTree.clear();
    }
}
