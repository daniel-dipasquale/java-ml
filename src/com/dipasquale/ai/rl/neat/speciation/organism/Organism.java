package com.dipasquale.ai.rl.neat.speciation.organism;

import com.dipasquale.ai.common.fitness.FitnessDeterminer;
import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.DefaultGenome;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.speciation.core.PopulationState;
import com.dipasquale.ai.rl.neat.speciation.core.Species;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class Organism implements Comparable<Organism>, Serializable {
    @Serial
    private static final long serialVersionUID = -1411966258093431863L;
    private static final double MINIMUM_COMPATIBILITY = Double.POSITIVE_INFINITY;
    @EqualsAndHashCode.Include
    private final DefaultGenome genome;
    private transient ProxyGenome proxyGenome;
    private final PopulationState populationState;
    private final SpeciesCompatibility speciesCompatibility;
    private transient FitnessState fitnessState;

    public Organism(final DefaultGenome genome, final PopulationState populationState) {
        this.genome = genome;
        this.proxyGenome = null;
        this.populationState = populationState;
        this.speciesCompatibility = new SpeciesCompatibility(MINIMUM_COMPATIBILITY, null);
        this.fitnessState = null;
    }

    private FitnessState createFitnessState(final Context.GeneralSupport general) {
        if (fitnessState == null) {
            return new FitnessState(general.createFitnessDeterminer(), 0f, populationState.getGeneration());
        }

        return new FitnessState(general.createFitnessDeterminer(), fitnessState.value, fitnessState.generation);
    }

    public void initialize(final Context context) {
        genome.initialize(context.neuralNetwork());
        proxyGenome = new ProxyGenome();
        fitnessState = createFitnessState(context.general());
    }

    public boolean isCompatible(final Context.SpeciationSupport speciation, final Species species) {
        double compatibility = speciation.calculateCompatibility(genome, species.getRepresentative().genome);

        if (Double.compare(speciesCompatibility.minimumCompatibility, compatibility) > 0) {
            speciesCompatibility.minimumCompatibility = compatibility;
            speciesCompatibility.species = species;
        }

        double compatibilityThreshold = speciation.compatibilityThreshold(populationState.getGeneration());

        return Double.compare(compatibility, compatibilityThreshold) < 0;
    }

    public Species getMostCompatibleSpecies() {
        return speciesCompatibility.species;
    }

    public void setMostCompatibleSpecies(final Species species) {
        speciesCompatibility.minimumCompatibility = MINIMUM_COMPATIBILITY;
        speciesCompatibility.species = species;
    }

    public float getFitness() {
        return fitnessState.value;
    }

    public int getComplexity() {
        return genome.getComplexity();
    }

    public float updateFitness(final Context.GeneralSupport general) {
        if (fitnessState.generation != populationState.getGeneration()) {
            fitnessState.generation = populationState.getGeneration();
            fitnessState.determiner.clear();
        }

        float fitnessNew = general.calculateFitness(proxyGenome);
        float fitnessNewFixed = !Float.isFinite(fitnessNew) ? 0f : Math.max(fitnessNew, 0f);

        fitnessState.determiner.add(fitnessNewFixed);

        return fitnessState.value = fitnessState.determiner.get();
    }

    public void mutate(final Context context) {
        genome.mutate(context);
    }

    @Override
    public int compareTo(final Organism other) {
        return Float.compare(fitnessState.value, other.fitnessState.value);
    }

    public Organism mate(final Context context, final Organism other) {
        int comparison = compareTo(other);

        DefaultGenome genomeNew = switch (comparison) {
            case 1 -> context.crossOver().crossOverBySkippingUnfitDisjointOrExcess(context, genome, other.genome);

            case 0 -> context.crossOver().crossOverByEqualTreatment(context, genome, other.genome);

            default -> context.crossOver().crossOverBySkippingUnfitDisjointOrExcess(context, other.genome, genome);
        };

        return new Organism(genomeNew, populationState);
    }

    public void freeze() {
        genome.freeze();
    }

    public float[] activate(final float[] inputs) {
        return genome.activate(inputs);
    }

    public Organism createCopy() {
        return new Organism(genome.createCopy(populationState.getHistoricalMarkings()), populationState);
    }

    public Organism createClone(final Context context) {
        DefaultGenome clonedGenome = genome.createClone(populationState.getHistoricalMarkings());
        Organism clonedOrganism = new Organism(clonedGenome, populationState);

        clonedOrganism.initialize(context);

        return clonedOrganism;
    }

    public void kill() {
        populationState.getHistoricalMarkings().markToKill(genome);
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    private static final class SpeciesCompatibility implements Serializable {
        @Serial
        private static final long serialVersionUID = -6165184849094919071L;
        private double minimumCompatibility;
        private Species species;
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class FitnessState {
        private final FitnessDeterminer determiner;
        private float value;
        private int generation;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class ProxyGenome implements Genome {
        @Override
        public String getId() {
            return genome.getId();
        }

        @Override
        public int getComplexity() {
            return genome.getComplexity();
        }

        @Override
        public float[] activate(final float[] input) {
            return genome.activate(input);
        }
    }
}