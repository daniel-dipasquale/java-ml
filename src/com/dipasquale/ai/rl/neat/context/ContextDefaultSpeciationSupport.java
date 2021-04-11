package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.genotype.GenomeCompatibilityCalculator;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefault;
import com.dipasquale.data.structure.map.SerializableInteroperableStateMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public final class ContextDefaultSpeciationSupport implements Context.SpeciationSupport {
    private int maximumSpecies;
    private int maximumGenomes;
    private float compatibilityThreshold;
    private float compatibilityThresholdModifier;
    private GenomeCompatibilityCalculator genomeCompatibilityCalculator;
    private float eugenicsThreshold;
    private float elitistThreshold;
    private int elitistThresholdMinimum;
    private int stagnationDropOffAge;
    private float interSpeciesMatingRate;

    @Override
    public int maximumSpecies() {
        return maximumSpecies;
    }

    @Override
    public int maximumGenomes() {
        return maximumGenomes;
    }

    @Override
    public double compatibilityThreshold(final int generation) { // TODO: check if this needs a boundary check as well
        return compatibilityThreshold * Math.pow(compatibilityThresholdModifier, generation);
    }

    @Override
    public double calculateCompatibility(final GenomeDefault genome1, final GenomeDefault genome2) {
        double compatibility = genomeCompatibilityCalculator.calculateCompatibility(genome1, genome2);

        if (Double.isFinite(compatibility)) {
            return compatibility;
        }

        return Double.MAX_VALUE;
    }

    @Override
    public float eugenicsThreshold() {
        return eugenicsThreshold;
    }

    @Override
    public float elitistThreshold() {
        return elitistThreshold;
    }

    @Override
    public int elitistThresholdMinimum() {
        return elitistThresholdMinimum;
    }

    @Override
    public int stagnationDropOffAge() {
        return stagnationDropOffAge;
    }

    @Override
    public float interSpeciesMatingRate() {
        return interSpeciesMatingRate;
    }

    public void save(final SerializableInteroperableStateMap state) {
        state.put("speciation.maximumSpecies", maximumSpecies);
        state.put("speciation.maximumGenomes", maximumGenomes);
        state.put("speciation.compatibilityThreshold", compatibilityThreshold);
        state.put("speciation.compatibilityThresholdModifier", compatibilityThresholdModifier);
        state.put("speciation.genomeCompatibilityCalculator", genomeCompatibilityCalculator);
        state.put("speciation.eugenicsThreshold", eugenicsThreshold);
        state.put("speciation.elitistThreshold", elitistThreshold);
        state.put("speciation.elitistThresholdMinimum", elitistThresholdMinimum);
        state.put("speciation.stagnationDropOffAge", stagnationDropOffAge);
        state.put("speciation.interSpeciesMatingRate", interSpeciesMatingRate);
    }

    public void load(final SerializableInteroperableStateMap state) {
        maximumSpecies = state.get("speciation.maximumSpecies");
        maximumGenomes = state.get("speciation.maximumGenomes");
        compatibilityThreshold = state.get("speciation.compatibilityThreshold");
        compatibilityThresholdModifier = state.get("speciation.compatibilityThresholdModifier");
        genomeCompatibilityCalculator = state.get("speciation.genomeCompatibilityCalculator");
        eugenicsThreshold = state.get("speciation.eugenicsThreshold");
        elitistThreshold = state.get("speciation.elitistThreshold");
        elitistThresholdMinimum = state.get("speciation.elitistThresholdMinimum");
        stagnationDropOffAge = state.get("speciation.stagnationDropOffAge");
        interSpeciesMatingRate = state.get("speciation.interSpeciesMatingRate");
    }
}
