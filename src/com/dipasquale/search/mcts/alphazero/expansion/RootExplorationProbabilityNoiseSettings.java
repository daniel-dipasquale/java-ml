package com.dipasquale.search.mcts.alphazero.expansion;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public final class RootExplorationProbabilityNoiseSettings {
    @Builder.Default
    private final float shape = ExplorationProbabilityNoiseRootExpansionPolicy.DEFAULT_SHAPE;
    @Builder.Default
    private final float epsilon = ExplorationProbabilityNoiseRootExpansionPolicy.DEFAULT_EPSILON;
}
