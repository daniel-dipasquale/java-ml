/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.fitness.FitnessDeterminer;
import com.dipasquale.ai.common.fitness.FitnessDeterminerFactory;
import com.dipasquale.ai.common.fitness.FitnessFunction;
import com.dipasquale.ai.rl.neat.genotype.DefaultGenome;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.common.SerializableInteroperableStateMap;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DefaultGeneralSupportContext implements Context.GeneralSupport {
    private int populationSize;
    private FitnessDeterminerFactory fitnessDeterminerFactory;
    private FitnessFunction<Genome> fitnessFunction;

    @Override
    public int populationSize() {
        return populationSize;
    }

    @Override
    public FitnessDeterminer createFitnessDeterminer() {
        return fitnessDeterminerFactory.create();
    }

    @Override
    public float calculateFitness(final DefaultGenome genome) {
        return fitnessFunction.test(genome);
    }

    public void save(final SerializableInteroperableStateMap state) {
        state.put("general.populationSize", populationSize);
        state.put("general.fitnessDeterminerFactory", fitnessDeterminerFactory);
        state.put("general.fitnessFunction", fitnessFunction);
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
        populationSize = state.get("general.populationSize");
        fitnessDeterminerFactory = state.get("general.fitnessDeterminerFactory");
        fitnessFunction = loadFitnessFunction(state, environmentOverride);
    }
}
