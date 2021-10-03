package com.dipasquale.ai.rl.neat.synchronization.dual.mode.speciation.strategy.fitness;

import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.SpeciesFitnessContext;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.SpeciesFitnessStrategy;
import com.dipasquale.synchronization.dual.mode.ConcurrencyLevelState;
import com.dipasquale.synchronization.dual.mode.DualModeObject;

import java.io.Serial;
import java.io.Serializable;

public final class DualModeSpeciesFitnessStrategy implements SpeciesFitnessStrategy, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 2445300240545685079L;
    private final ConcurrencyLevelState concurrencyLevelState;
    private final SpeciesFitnessStrategy concurrentStrategy;
    private final SpeciesFitnessStrategy defaultStrategy;

    public DualModeSpeciesFitnessStrategy(final int concurrencyLevel, final SpeciesFitnessStrategy concurrentStrategy, final SpeciesFitnessStrategy defaultStrategy) {
        this.concurrencyLevelState = new ConcurrencyLevelState(concurrencyLevel);
        this.concurrentStrategy = concurrentStrategy;
        this.defaultStrategy = defaultStrategy;
    }

    @Override
    public void update(final SpeciesFitnessContext context) {
        if (concurrencyLevelState.getCurrent() > 0) {
            concurrentStrategy.update(context);
        } else {
            defaultStrategy.update(context);
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
