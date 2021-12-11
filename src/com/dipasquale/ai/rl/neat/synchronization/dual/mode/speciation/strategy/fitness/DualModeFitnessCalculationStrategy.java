package com.dipasquale.ai.rl.neat.synchronization.dual.mode.speciation.strategy.fitness;

import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.FitnessCalculationContext;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.FitnessCalculationStrategy;
import com.dipasquale.synchronization.dual.mode.ConcurrencyLevelState;
import com.dipasquale.synchronization.dual.mode.DualModeObject;

import java.io.Serial;
import java.io.Serializable;

public final class DualModeFitnessCalculationStrategy implements FitnessCalculationStrategy, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 2445300240545685079L;
    private final ConcurrencyLevelState concurrencyLevelState;
    private final FitnessCalculationStrategy concurrentStrategy;
    private final FitnessCalculationStrategy defaultStrategy;

    public DualModeFitnessCalculationStrategy(final int concurrencyLevel, final FitnessCalculationStrategy concurrentStrategy, final FitnessCalculationStrategy defaultStrategy) {
        this.concurrencyLevelState = new ConcurrencyLevelState(concurrencyLevel);
        this.concurrentStrategy = concurrentStrategy;
        this.defaultStrategy = defaultStrategy;
    }

    @Override
    public void calculate(final FitnessCalculationContext context) {
        if (concurrencyLevelState.getCurrent() > 0) {
            concurrentStrategy.calculate(context);
        } else {
            defaultStrategy.calculate(context);
        }
    }

    @Override
    public int concurrencyLevel() {
        return concurrencyLevelState.getCurrent();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        concurrencyLevelState.setCurrent(concurrencyLevel);
    }
}
