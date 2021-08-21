package com.dipasquale.ai.rl.neat.speciation.strategy.fitness;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

@RequiredArgsConstructor
public final class MultiSpeciesFitnessStrategy implements SpeciesFitnessStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = -2457394370282288023L;
    private final Collection<SpeciesFitnessStrategy> strategies;

    @Override
    public void update(final SpeciesFitnessContext context) {
        for (SpeciesFitnessStrategy strategy : strategies) {
            strategy.update(context);
        }
    }
}
