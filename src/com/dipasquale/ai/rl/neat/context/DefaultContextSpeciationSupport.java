package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.GenomeCompatibilityCalculator;
import com.dipasquale.ai.rl.neat.speciation.core.ReproductionType;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.SpeciesFitnessStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.SpeciesReproductionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SpeciesSelectionStrategyExecutor;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.DualModeSequentialIdFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype.DualModeGenomeHub;
import com.dipasquale.common.SerializableInteroperableStateMap;
import com.dipasquale.common.factory.ObjectIndexer;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DefaultContextSpeciationSupport implements Context.SpeciationSupport {
    private DefaultContextSpeciationParameters params;
    private DualModeSequentialIdFactory speciesIdFactory;
    private DualModeGenomeHub genomeHub;
    private GenomeCompatibilityCalculator genomeCompatibilityCalculator;
    private ObjectProfile<ObjectIndexer<ReproductionType>> reproductionTypeFactory;
    private ObjectProfile<SpeciesFitnessStrategy> fitnessStrategy;
    private ObjectProfile<SpeciesSelectionStrategyExecutor> selectionStrategy;
    private ObjectProfile<SpeciesReproductionStrategy> reproductionStrategy;

    @Override
    public Context.SpeciationParameters params() {
        return params;
    }

    @Override
    public String createSpeciesId() {
        return speciesIdFactory.create().toString();
    }

    @Override
    public String createGenomeId() {
        return genomeHub.createGenomeId();
    }

    @Override
    public Genome createGenome(final Context context) {
        return genomeHub.createGenome(context);
    }

    @Override
    public double calculateCompatibility(final Genome genome1, final Genome genome2) {
        double compatibility = genomeCompatibilityCalculator.calculateCompatibility(genome1, genome2);

        if (compatibility == Double.POSITIVE_INFINITY) {
            return Double.MAX_VALUE;
        }

        if (compatibility == Double.NEGATIVE_INFINITY) {
            return -Double.MAX_VALUE;
        }

        return compatibility;
    }

    @Override
    public ReproductionType generateReproductionType(final int organisms) {
        return reproductionTypeFactory.getObject().get(organisms);
    }

    @Override
    public SpeciesFitnessStrategy getFitnessStrategy() {
        return fitnessStrategy.getObject();
    }

    @Override
    public SpeciesSelectionStrategyExecutor getSelectionStrategy() {
        return selectionStrategy.getObject();
    }

    @Override
    public SpeciesReproductionStrategy getReproductionStrategy() {
        return reproductionStrategy.getObject();
    }

    @Override
    public int getGenomeKilledCount() {
        return genomeHub.getGenomeKilledCount();
    }

    @Override
    public void markToKill(final Genome genome) {
        genomeHub.markToKill(genome);
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
