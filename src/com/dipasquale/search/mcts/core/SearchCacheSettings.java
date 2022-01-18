package com.dipasquale.search.mcts.core;

import com.dipasquale.common.ArgumentValidatorSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SearchCacheSettings {
    private final int participants;

    <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> SearchNodeCache<TAction, TEdge, TState> create(final EdgeFactory<TEdge> edgeFactory) {
        ArgumentValidatorSupport.ensureGreaterThanZero(participants, "participants");

        return new SearchNodeCache<>(participants, edgeFactory);
    }
}
