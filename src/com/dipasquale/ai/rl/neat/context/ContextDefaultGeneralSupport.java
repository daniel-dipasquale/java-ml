package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.FitnessDeterminer;
import com.dipasquale.ai.common.FitnessDeterminerFactory;
import com.dipasquale.ai.rl.neat.NeatEnvironment;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefault;
import com.dipasquale.data.structure.map.SerializableInteroperableStateMap;
import lombok.AllArgsConstructor;

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

    public void save(final SerializableInteroperableStateMap state) {
        state.put("general.populationSize", populationSize);
        state.put("general.fitnessDeterminerFactory", fitnessDeterminerFactory);
        state.put("general.environment", environment);
    }

    private NeatEnvironment loadEnvironment(final SerializableInteroperableStateMap state, final NeatEnvironment environmentOverride) {
        if (environmentOverride != null) {
            return null;
        }

        Object environment = state.get("general.environment");

        if (environment instanceof NeatEnvironment) {
            return (NeatEnvironment) environment;
        }

        String message = "unable to load the environment (fitness function)";

        if (environment instanceof Exception) {
            throw new IllegalStateException(message, (Exception) environment);
        }

        throw new IllegalStateException(message);
    }

    public void load(final SerializableInteroperableStateMap state, final NeatEnvironment environmentOverride) {
        populationSize = state.get("general.populationSize");
        fitnessDeterminerFactory = state.get("general.fitnessDeterminerFactory");
        environment = loadEnvironment(state, environmentOverride);
    }
}
