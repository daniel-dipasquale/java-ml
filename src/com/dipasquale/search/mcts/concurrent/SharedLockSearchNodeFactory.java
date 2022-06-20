package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNodeFactory;
import com.dipasquale.search.mcts.SearchResult;
import com.dipasquale.search.mcts.State;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class SharedLockSearchNodeFactory<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> implements SearchNodeFactory<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    @Getter
    private final EdgeFactory<TEdge> edgeFactory;
    private final List<Long> threadIds;

    @Override
    public ConcurrentSearchNode<TAction, TEdge, TState> createRoot(final SearchResult<TAction, TState> result) {
        return new SharedLockSearchNode<>(result, edgeFactory.create(), threadIds);
    }
}
