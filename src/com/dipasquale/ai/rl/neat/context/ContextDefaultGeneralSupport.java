package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.FitnessDeterminer;
import com.dipasquale.ai.common.FitnessDeterminerFactory;
import com.dipasquale.ai.rl.neat.NeatEnvironment;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefault;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public final class ContextDefaultGeneralSupport implements Context.GeneralSupport {
    private int populationSize;
    private FitnessDeterminerFactory fitnessDeterminerFactory;
    private NeatEnvironment environment;

    @Override
    public int populationSize() {
        return populationSize;
    }

    @Override
    public FitnessDeterminer createFitnessDeterminer() {
        return fitnessDeterminerFactory.create();
    }

    @Override
    public float calculateFitness(final GenomeDefault genome) {
        return environment.test(genome);
    }

    public void save(final ContextDefaultStateMap state) {
        state.put("general.populationSize", populationSize);
        state.put("general.fitnessDeterminerFactory", fitnessDeterminerFactory);
        state.put("general.environment", environment);
    }

    public void load(final ContextDefaultStateMap state) {
        populationSize = state.get("general.populationSize");
        fitnessDeterminerFactory = state.get("general.fitnessDeterminerFactory");
        environment = state.get("general.environment");
    }
}
