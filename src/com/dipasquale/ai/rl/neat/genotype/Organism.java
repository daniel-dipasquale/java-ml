package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.FitnessDeterminer;
import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.population.Population;
import com.dipasquale.ai.rl.neat.species.Species;
import lombok.Getter;

public final class Organism implements Comparable<Organism> {
    private final GenomeDefault genome;
    private final Population population;
    private double minimumCompatibility;
    @Getter
    private Species mostCompatibleSpecies;
    private final FitnessDeterminer fitnessDeterminer;
    private int fitnessGeneration;

    public Organism(final GenomeDefault genome, final Population population, final Context.GeneralSupport general) {
        this.genome = genome;
        this.population = population;
        this.minimumCompatibility = Double.POSITIVE_INFINITY;
        this.mostCompatibleSpecies = null;
        this.fitnessDeterminer = general.createFitnessDeterminer();
        this.fitnessGeneration = -1;
    }

    public boolean isCompatible(final Context.Speciation speciation, final Species species) {
        double compatibility = speciation.calculateCompatibility(genome, species.getRepresentative().genome);

        if (Double.compare(minimumCompatibility, compatibility) > 0) {
            minimumCompatibility = compatibility;
            mostCompatibleSpecies = species;
        }

        double compatibilityThreshold = speciation.compatibilityThreshold(population.getGeneration());

        return Double.compare(compatibility, compatibilityThreshold) < 0;
    }

    public void setMostCompatibleSpecies(final Species species) {
        minimumCompatibility = Double.POSITIVE_INFINITY;
        mostCompatibleSpecies = species;
    }

    public float getFitness() {
        return fitnessDeterminer.get();
    }

    public float updateFitness(final Context.GeneralSupport general) {
        if (fitnessGeneration != population.getGeneration()) {
            fitnessGeneration = population.getGeneration();
            fitnessDeterminer.clear();
        }

        float fitness = general.calculateFitness(genome);
        float fitnessFixed = !Float.isFinite(fitness) ? 0f : Math.max(fitness, 0f);

        fitnessDeterminer.add(fitnessFixed);

        return fitnessDeterminer.get();
    }

    public void mutate(final Context context) {
        genome.mutate(context);
    }

    @Override
    public int compareTo(final Organism other) {
        return Float.compare(fitnessDeterminer.get(), other.fitnessDeterminer.get());
    }

    public Organism mate(final Context context, final Organism other) {
        int comparison = compareTo(other);

        GenomeDefault genomeNew = switch (comparison) {
            case 1 -> context.crossOver().crossOverBySkippingUnfitDisjointOrExcess(context, genome, other.genome);

            case 0 -> context.crossOver().crossOverByEqualTreatment(context, genome, other.genome);

            default -> context.crossOver().crossOverBySkippingUnfitDisjointOrExcess(context, other.genome, genome);
        };

        return new Organism(genomeNew, population, context.general());
    }

    public void freeze() {
        genome.freeze();
    }

    public float[] activate(final float[] inputs) {
        return genome.activate(inputs);
    }

    public Organism createCopy(final Context context) {
        return new Organism(genome.createCopy(context), population, context.general());
    }

    public Organism createClone(final Context context) {
        return new Organism(genome.createClone(context.neuralNetwork()), population, context.general());
    }

    public void kill(final Context.GeneralSupport general) {
        general.markToKill(genome);
    }
}