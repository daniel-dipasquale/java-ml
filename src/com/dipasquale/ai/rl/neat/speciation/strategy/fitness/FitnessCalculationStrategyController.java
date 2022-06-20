package com.dipasquale.ai.rl.neat.speciation.strategy.fitness;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

@RequiredArgsConstructor
public final class FitnessCalculationStrategyController implements FitnessCalculationStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = -2457394370282288023L;
    private final Collection<FitnessCalculationStrategy> strategies;

    @Override
    public void calculate(final FitnessCalculationContext context) {
        for (FitnessCalculationStrategy strategy : strategies) {
            strategy.calculate(context);
        }
    }
}
