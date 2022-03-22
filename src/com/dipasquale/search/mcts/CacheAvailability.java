package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CacheAvailability {
    DISABLED,
    ENABLED;

    public <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> Cache<TAction, TEdge, TState> provide(final EdgeFactory<TEdge> edgeFactory) {
        return switch (this) {
            case DISABLED -> null;

            case ENABLED -> new Cache<>(edgeFactory);
        };
    }
}
