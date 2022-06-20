package com.dipasquale.search.mcts.buffer;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeFactory;
import com.dipasquale.search.mcts.SearchNodeResult;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.initialization.InitializationContext;

final class DisabledBuffer<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements Buffer<TAction, TEdge, TState, TSearchNode> {
    private final SearchNodeFactory<TAction, TEdge, TState, TSearchNode> searchNodeFactory;
    private final EdgeFactory<TEdge> edgeFactory;

    DisabledBuffer(final InitializationContext<TAction, TEdge, TState, TSearchNode> initializationContext) {
        this.searchNodeFactory = initializationContext.getSearchNodeFactory();
        this.edgeFactory = initializationContext.getEdgeFactory();
    }

    @Override
    public TSearchNode provide(final SearchNodeResult<TAction, TState> searchNodeResult) {
        return searchNodeFactory.createRoot(searchNodeResult, edgeFactory);
    }

    @Override
    public void put(final TSearchNode searchNode) {
    }

    @Override
    public void clear() {
    }
}
