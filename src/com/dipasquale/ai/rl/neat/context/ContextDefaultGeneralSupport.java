package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.FitnessDeterminer;
import com.dipasquale.ai.common.FitnessDeterminerFactory;
import com.dipasquale.ai.rl.neat.NeatEnvironment;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefault;
import com.dipasquale.data.structure.map.SerializableInteroperableStateMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Optional;

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

    public void save(final SerializableInteroperableStateMap state) {
        state.put("general.populationSize", populationSize);
        state.put("general.fitnessDeterminerFactory", fitnessDeterminerFactory);
        state.put("general.environment", environment);
    }

    public void load(final SerializableInteroperableStateMap state, final NeatEnvironment environmentOverride) {
        populationSize = state.get("general.populationSize");
        fitnessDeterminerFactory = state.get("general.fitnessDeterminerFactory");

        environment = Optional.ofNullable(environmentOverride)
                .orElseGet(() -> state.get("general.environment"));
    }
}
