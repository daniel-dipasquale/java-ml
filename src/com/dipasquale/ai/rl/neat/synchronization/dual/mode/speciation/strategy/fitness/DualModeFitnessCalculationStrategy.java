package com.dipasquale.ai.rl.neat.synchronization.dual.mode.speciation.strategy.fitness;

import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.FitnessCalculationContext;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.FitnessCalculationStrategy;
import com.dipasquale.synchronization.dual.mode.DualModeObject;

import java.io.Serial;
import java.io.Serializable;

public final class DualModeFitnessCalculationStrategy implements FitnessCalculationStrategy, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 2445300240545685079L;
    private final FitnessCalculationStrategy concurrentStrategy;
    private final FitnessCalculationStrategy defaultStrategy;
    private FitnessCalculationStrategy selectedStrategy;

    private static FitnessCalculationStrategy select(final int concurrencyLevel, final FitnessCalculationStrategy concurrentStrategy, final FitnessCalculationStrategy defaultStrategy) {
        if (concurrencyLevel > 0) {
            return concurrentStrategy;
        }

        return defaultStrategy;
    }

    public DualModeFitnessCalculationStrategy(final int concurrencyLevel, final FitnessCalculationStrategy concurrentStrategy, final FitnessCalculationStrategy defaultStrategy) {
        this.concurrentStrategy = concurrentStrategy;
        this.defaultStrategy = defaultStrategy;
        this.selectedStrategy = select(concurrencyLevel, concurrentStrategy, defaultStrategy);
    }

    @Override
    public void calculate(final FitnessCalculationContext context) {
        selectedStrategy.calculate(context);
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        selectedStrategy = select(concurrencyLevel, concurrentStrategy, defaultStrategy);
    }
}
