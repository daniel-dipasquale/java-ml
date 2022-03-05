package com.dipasquale.search.mcts;

import com.dipasquale.common.ArgumentValidatorSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SearchNodeCacheSettings {
    private final int participants;

    public <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> CacheSearchNodeProvider<TAction, TEdge, TState> create(final EdgeFactory<TEdge> edgeFactory) {
        ArgumentValidatorSupport.ensureGreaterThanZero(participants, "participants");

        return new CacheSearchNodeProvider<>(participants, edgeFactory);
    }
}
