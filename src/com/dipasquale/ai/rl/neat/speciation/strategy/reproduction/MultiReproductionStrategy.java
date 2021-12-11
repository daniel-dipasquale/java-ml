package com.dipasquale.ai.rl.neat.speciation.strategy.reproduction;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

@RequiredArgsConstructor
public final class MultiReproductionStrategy implements ReproductionStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = 7605352967985128776L;
    private final Collection<ReproductionStrategy> strategies;

    @Override
    public void reproduce(final ReproductionContext context) {
        for (ReproductionStrategy strategy : strategies) {
            strategy.reproduce(context);
        }
    }
}
