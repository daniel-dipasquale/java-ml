package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.fitness.FitnessDeterminer;
import com.dipasquale.ai.common.fitness.FitnessDeterminerFactory;
import com.dipasquale.ai.common.fitness.FitnessFunction;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.common.SerializableInteroperableStateMap;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DefaultContextGeneralSupport implements Context.GeneralSupport {
    private DefaultContextGeneralParameters params;
    private FitnessFunction<Genome> fitnessFunction;
    private FitnessDeterminerFactory fitnessDeterminerFactory;

    @Override
    public Context.GeneralParams params() {
        return params;
    }

    @Override
    public float calculateFitness(final Genome genome) {
        return fitnessFunction.test(genome);
    }

    @Override
    public FitnessDeterminer createFitnessDeterminer() {
        return fitnessDeterminerFactory.create();
    }

    public void save(final SerializableInteroperableStateMap state) {
        state.put("general.params", params);
        state.put("general.fitnessFunction", fitnessFunction);
        state.put("general.fitnessDeterminerFactory", fitnessDeterminerFactory);
    }

    private FitnessFunction<Genome> loadFitnessFunction(final SerializableInteroperableStateMap state, final FitnessFunction<Genome> loadFitnessFunctionOverride) {
        if (loadFitnessFunctionOverride != null) {
            return null;
        }

        Object fitnessFunction = state.get("general.fitnessFunction");

        if (fitnessFunction instanceof FitnessFunction<?>) {
            return (FitnessFunction<Genome>) fitnessFunction;
        }

        String message = "unable to load the fitness function";

        if (fitnessFunction instanceof Throwable) {
            throw new IllegalStateException(message, (Throwable) fitnessFunction);
        }

        throw new IllegalStateException(message);
    }

    public void load(final SerializableInteroperableStateMap state, final FitnessFunction<Genome> environmentOverride) {
        params = state.get("general.params");
        fitnessFunction = loadFitnessFunction(state, environmentOverride);
        fitnessDeterminerFactory = state.get("general.fitnessDeterminerFactory");
    }
}
