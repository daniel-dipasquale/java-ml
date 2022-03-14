package com.dipasquale.search.mcts;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public final class NodeCacheSettings {
    private final int participants;
}
