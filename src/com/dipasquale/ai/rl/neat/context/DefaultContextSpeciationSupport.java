package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.GenomeCompatibilityCalculator;
import com.dipasquale.ai.rl.neat.speciation.core.ReproductionType;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.SpeciesFitnessStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.SpeciesReproductionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SpeciesSelectionStrategyExecutor;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.DualModeSequentialIdFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype.DualModeGenomePool;
import com.dipasquale.common.factory.ObjectIndexer;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DefaultContextSpeciationSupport implements Context.SpeciationSupport {
    private DefaultContextSpeciationParameters params;
    private DualModeSequentialIdFactory speciesIdFactory;
    private DualModeGenomePool genomePool;
    private GenomeCompatibilityCalculator genomeCompatibilityCalculator;
    private ObjectProfile<ObjectIndexer<ReproductionType>> reproductionTypeFactoryProfile;
    private ObjectProfile<SpeciesFitnessStrategy> fitnessStrategyProfile;
    private ObjectProfile<SpeciesSelectionStrategyExecutor> selectionStrategyProfile;
    private ObjectProfile<SpeciesReproductionStrategy> reproductionStrategyProfile;

    @Override
    public Context.SpeciationParameters params() {
        return params;
    }

    @Override
    public String createSpeciesId() {
        return speciesIdFactory.create().toString();
    }

    @Override
    public void clearSpeciesIds() {
        speciesIdFactory.reset();
    }

    @Override
    public String createGenomeId() {
        return genomePool.createId();
    }

    @Override
    public void clearGenomeIds() {
        genomePool.clearIds();
    }

    @Override
    public Genome createGenesisGenome(final Context context) {
        return genomePool.createGenesis(context);
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
        return reproductionTypeFactoryProfile.getObject().get(organisms);
    }

    @Override
    public SpeciesFitnessStrategy getFitnessStrategy() {
        return fitnessStrategyProfile.getObject();
    }

    @Override
    public SpeciesSelectionStrategyExecutor getSelectionStrategy() {
        return selectionStrategyProfile.getObject();
    }

    @Override
    public SpeciesReproductionStrategy getReproductionStrategy() {
        return reproductionStrategyProfile.getObject();
    }

    @Override
    public void disposeGenomeId(final Genome genome) {
        genomePool.disposeId(genome);
    }

    @Override
    public int getDisposedGenomeIdCount() {
        return genomePool.getDisposedCount();
    }

    public void save(final SerializableStateGroup state) {
        state.put("speciation.params", params);
        state.put("speciation.speciesIdFactory", speciesIdFactory);
        state.put("speciation.genomePool", genomePool);
        state.put("speciation.genomeCompatibilityCalculator", genomeCompatibilityCalculator);
        state.put("speciation.reproductionTypeFactoryProfile", reproductionTypeFactoryProfile);
        state.put("speciation.fitnessStrategyProfile", fitnessStrategyProfile);
        state.put("speciation.selectionStrategyProfile", selectionStrategyProfile);
        state.put("speciation.reproductionStrategyProfile", reproductionStrategyProfile);
    }

    public void load(final SerializableStateGroup state, final IterableEventLoop eventLoop) {
        params = state.get("speciation.params");
        speciesIdFactory = DualModeObject.switchMode(state.get("speciation.speciesIdFactory"), eventLoop != null);
        genomePool = DualModeObject.switchMode(state.get("speciation.genomePool"), eventLoop != null);
        genomeCompatibilityCalculator = state.get("speciation.genomeCompatibilityCalculator");
        reproductionTypeFactoryProfile = ObjectProfile.switchProfile(state.get("speciation.reproductionTypeFactoryProfile"), eventLoop != null);
        fitnessStrategyProfile = ObjectProfile.switchProfile(state.get("speciation.fitnessStrategyProfile"), eventLoop != null);
        selectionStrategyProfile = ObjectProfile.switchProfile(state.get("speciation.selectionStrategyProfile"), eventLoop != null);
        reproductionStrategyProfile = ObjectProfile.switchProfile(state.get("speciation.reproductionStrategyProfile"), eventLoop != null);
    }
}
