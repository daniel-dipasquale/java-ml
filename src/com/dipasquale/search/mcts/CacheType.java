package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CacheType {
    NONE,
    AUTO_CLEAR;

    public <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> Provider<TAction, TEdge, TState> create(final EdgeFactory<TEdge> edgeFactory) {
        return switch (this) {
            case NONE -> new ZeroCollectionProvider<>(edgeFactory);

            case AUTO_CLEAR -> new AutoClearCacheProvider<>(edgeFactory);
        };
    }
}
