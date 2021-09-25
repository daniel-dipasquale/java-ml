package com.dipasquale.ai.rl.neat.speciation.strategy.reproduction;

import com.dipasquale.ai.rl.neat.speciation.core.Species;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public final class SpeciesState {
    private final List<Species> all;
    private final List<Species> ranked;
    private final float totalSharedFitness;
}
