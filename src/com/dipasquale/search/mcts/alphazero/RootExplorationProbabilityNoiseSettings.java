package com.dipasquale.search.mcts.alphazero;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public final class RootExplorationProbabilityNoiseSettings {
    @Builder.Default
    private final float shape = ExplorationProbabilityNoiseRootSearchNodeInitializer.DEFAULT_SHAPE;
    @Builder.Default
    private final float epsilon = ExplorationProbabilityNoiseRootSearchNodeInitializer.DEFAULT_EPSILON;
}
