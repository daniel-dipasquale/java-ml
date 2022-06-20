package com.dipasquale.search.mcts.buffer;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeFactory;
import com.dipasquale.search.mcts.SearchNodeResult;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.initialization.InitializationContext;

final class AutoClearBuffer<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements Buffer<TAction, TEdge, TState, TSearchNode> {
    private int ownerParticipantId;
    private final GenerationTree<TAction, TEdge, TState, TSearchNode> generationTree;
    private final SearchNodeFactory<TAction, TEdge, TState, TSearchNode> searchNodeFactory;
    private final EdgeFactory<TEdge> edgeFactory;

    AutoClearBuffer(final InitializationContext<TAction, TEdge, TState, TSearchNode> initializationContext) {
        this.ownerParticipantId = Integer.MIN_VALUE;
        this.generationTree = new GenerationTree<>();
        this.searchNodeFactory = initializationContext.getSearchNodeFactory();
        this.edgeFactory = initializationContext.getEdgeFactory();
    }

    private TSearchNode getOrCreateRoot(final SearchNodeResult<TAction, TState> searchNodeResult) {
        if (searchNodeResult.getState().getNextParticipantId() == ownerParticipantId) {
            TSearchNode searchNode = generationTree.reseed(searchNodeResult.getStateId());

            if (searchNode == null) {
                searchNode = searchNodeFactory.createRoot(searchNodeResult, edgeFactory);
                generationTree.seed(searchNode);
            } else {
                searchNode.reinitialize(searchNodeResult);
            }

            return searchNode;
        }

        TSearchNode searchNode = searchNodeFactory.createRoot(searchNodeResult, edgeFactory);

        generationTree.seed(searchNode);

        return searchNode;
    }

    @Override
    public TSearchNode provide(final SearchNodeResult<TAction, TState> searchNodeResult) {
        TSearchNode searchNode = getOrCreateRoot(searchNodeResult);

        ownerParticipantId = searchNodeResult.getState().getNextParticipantId();

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
