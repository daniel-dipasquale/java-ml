package com.dipasquale.ai.rl.neat.speciation.strategy.reproduction;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

@RequiredArgsConstructor
public final class MultiSpeciesReproductionStrategy implements SpeciesReproductionStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = 7605352967985128776L;
    private final Collection<SpeciesReproductionStrategy> strategies;

    @Override
    public void reproduce(final SpeciesReproductionContext context) {
        for (SpeciesReproductionStrategy strategy : strategies) {
            strategy.reproduce(context);
        }
    }
}
