package com.dipasquale.ai.rl.neat.speciation;

import com.dipasquale.ai.common.fitness.FitnessDeterminer;
import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.DefaultGenome;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class Organism implements Comparable<Organism>, Serializable {
    @Serial
    private static final long serialVersionUID = -1411966258093431863L;
    private static final double MINIMUM_COMPATIBILITY = Double.POSITIVE_INFINITY;
    @EqualsAndHashCode.Include
    private final DefaultGenome genome;
    private final PopulationInfo population;
    private final MostCompatibleSpecies mostCompatibleSpecies;
    private transient Fitness fitness;

    public Organism(final DefaultGenome genome, final PopulationInfo population) {
        this.genome = genome;
        this.population = population;
        this.mostCompatibleSpecies = new MostCompatibleSpecies(MINIMUM_COMPATIBILITY, null);
        this.fitness = null;
    }

    private Fitness createFitness(final Context.GeneralSupport general) {
        if (fitness == null) {
            return new Fitness(general.createFitnessDeterminer(), 0f, population.getGeneration());
        }

        return new Fitness(general.createFitnessDeterminer(), fitness.value, fitness.generation);
    }

    public void prepare(final Context context) {
        genome.prepare(context.neuralNetwork());
        fitness = createFitness(context.general());
    }

    public Species getMostCompatibleSpecies() {
        return mostCompatibleSpecies.reference;
    }

    public boolean isCompatible(final Context.SpeciationSupport speciation, final Species species) {
        double compatibility = speciation.calculateCompatibility(genome, species.getRepresentative().genome);

        if (Double.compare(mostCompatibleSpecies.minimum, compatibility) > 0) {
            mostCompatibleSpecies.minimum = compatibility;
            mostCompatibleSpecies.reference = species;
        }

        double compatibilityThreshold = speciation.compatibilityThreshold(population.getGeneration());

        return Double.compare(compatibility, compatibilityThreshold) < 0;
    }

    public void setMostCompatibleSpecies(final Species species) {
        mostCompatibleSpecies.minimum = MINIMUM_COMPATIBILITY;
        mostCompatibleSpecies.reference = species;
    }

    public float getFitness() {
        return fitness.value;
    }

    public float updateFitness(final Context.GeneralSupport general) {
        if (fitness.generation != population.getGeneration()) {
            fitness.generation = population.getGeneration();
            fitness.determiner.clear();
        }

        float fitnessNew = general.calculateFitness(genome);
        float fitnessNewFixed = !Float.isFinite(fitnessNew) ? 0f : Math.max(fitnessNew, 0f);

        fitness.determiner.add(fitnessNewFixed);

        return fitness.value = fitness.determiner.get();
    }

    public void mutate(final Context context) {
        genome.mutate(context);
    }

    @Override
    public int compareTo(final Organism other) {
        return Float.compare(fitness.value, other.fitness.value);
    }

    public Organism mate(final Context context, final Organism other) {
        int comparison = compareTo(other);

        DefaultGenome genomeNew = switch (comparison) {
            case 1 -> context.crossOver().crossOverBySkippingUnfitDisjointOrExcess(context, genome, other.genome);

            case 0 -> context.crossOver().crossOverByEqualTreatment(context, genome, other.genome);

            default -> context.crossOver().crossOverBySkippingUnfitDisjointOrExcess(context, other.genome, genome);
        };

        return new Organism(genomeNew, population);
    }

    public void freeze() {
        genome.freeze();
    }

    public float[] activate(final float[] inputs) {
        return genome.activate(inputs);
    }

    public Organism createCopy() {
        return new Organism(genome.createCopy(population.getHistoricalMarkings()), population);
    }

    public Organism createClone(final Context context) {
        Organism organism = new Organism(genome.createClone(population.getHistoricalMarkings()), population);

        organism.prepare(context);

        return organism;
    }

    public void kill() {
        population.getHistoricalMarkings().markToKill(genome);
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    private static final class MostCompatibleSpecies implements Serializable {
        @Serial
        private static final long serialVersionUID = -6165184849094919071L;
        private double minimum;
        private Species reference;
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Fitness {
        private final FitnessDeterminer determiner;
        private float value;
        private int generation;
    }
}