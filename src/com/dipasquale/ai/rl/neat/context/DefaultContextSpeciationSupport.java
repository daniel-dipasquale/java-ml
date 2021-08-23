package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.genotype.DefaultGenome;
import com.dipasquale.ai.rl.neat.genotype.GenomeCompatibilityCalculator;
import com.dipasquale.ai.rl.neat.speciation.core.ReproductionType;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.SpeciesFitnessStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.SpeciesReproductionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SpeciesSelectionStrategyExecutor;
import com.dipasquale.common.SerializableInteroperableStateMap;
import com.dipasquale.common.factory.ObjectAccessor;
import com.dipasquale.common.profile.ObjectProfile;
import com.dipasquale.threading.event.loop.IterableEventLoop;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DefaultContextSpeciationSupport implements Context.SpeciationSupport {
    private DefaultContextSpeciationParameters params;
    private GenomeCompatibilityCalculator genomeCompatibilityCalculator;
    private ObjectProfile<ObjectAccessor<ReproductionType>> reproductionTypeFactory;
    private ObjectProfile<SpeciesFitnessStrategy> fitnessStrategy;
    private ObjectProfile<SpeciesSelectionStrategyExecutor> selectionStrategy;
    private ObjectProfile<SpeciesReproductionStrategy> reproductionStrategy;

    @Override
    public Context.SpeciationParameters params() {
        return params;
    }

    @Override
    public double calculateCompatibility(final DefaultGenome genome1, final DefaultGenome genome2) {
        double compatibility = genomeCompatibilityCalculator.calculateCompatibility(genome1, genome2);

        if (compatibility == Double.POSITIVE_INFINITY) {
            return Float.MAX_VALUE;
        }

        if (compatibility == Double.NEGATIVE_INFINITY) {
            return Float.MIN_VALUE;
        }

        return compatibility;
    }

    @Override
    public ReproductionType nextReproductionType(final int organisms) {
        return reproductionTypeFactory.getObject().get(organisms);
    }

    @Override
    public SpeciesFitnessStrategy fitnessStrategy() {
        return fitnessStrategy.getObject();
    }

    @Override
    public SpeciesSelectionStrategyExecutor selectionStrategy() {
        return selectionStrategy.getObject();
    }

    @Override
    public SpeciesReproductionStrategy reproductionStrategy() {
        return reproductionStrategy.getObject();
    }

    public void save(final SerializableInteroperableStateMap state) {
        state.put("speciation.params", params);
        state.put("speciation.genomeCompatibilityCalculator", genomeCompatibilityCalculator);
        state.put("speciation.reproductionTypeFactory", reproductionTypeFactory);
        state.put("speciation.fitnessStrategy", fitnessStrategy);
        state.put("speciation.selectionStrategy", selectionStrategy);
        state.put("speciation.reproductionStrategy", reproductionStrategy);
    }

    public void load(final SerializableInteroperableStateMap state, final IterableEventLoop eventLoop) {
        params = state.get("speciation.params");
        genomeCompatibilityCalculator = state.get("speciation.genomeCompatibilityCalculator");
        reproductionTypeFactory = ObjectProfile.switchProfile(state.get("speciation.reproductionTypeFactory"), eventLoop != null);
        fitnessStrategy = ObjectProfile.switchProfile(state.get("speciation.fitnessStrategy"), eventLoop != null);
        selectionStrategy = ObjectProfile.switchProfile(state.get("speciation.selectionStrategy"), eventLoop != null);
        reproductionStrategy = ObjectProfile.switchProfile(state.get("speciation.reproductionStrategy"), eventLoop != null);
    }
}
