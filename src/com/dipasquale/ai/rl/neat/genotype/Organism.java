package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.FitnessDeterminer;
import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.population.Population;
import com.dipasquale.ai.rl.neat.species.Species;
import lombok.Getter;

public final class Organism implements Comparable<Organism> {
    private final Context context;
    private final Population population;
    private final GenomeDefault genome;
    private double minimumCompatibility;
    @Getter
    private Species mostCompatibleSpecies;
    private final FitnessDeterminer fitnessDeterminer;
    private int fitnessGeneration;

    public Organism(final Context context, final Population population, final GenomeDefault genome) {
        this.context = context;
        this.population = population;
        this.genome = genome;
        this.minimumCompatibility = Double.POSITIVE_INFINITY;
        this.mostCompatibleSpecies = null;
        this.fitnessDeterminer = context.general().createFitnessDeterminer();
        this.fitnessGeneration = -1;
    }

    public boolean isCompatible(final Species species) {
        double compatibility = context.speciation().calculateCompatibility(genome, species.getRepresentative().genome);

        if (Double.compare(minimumCompatibility, compatibility) > 0) {
            minimumCompatibility = compatibility;
            mostCompatibleSpecies = species;
        }

        double compatibilityThreshold = context.speciation().compatibilityThreshold(population.getGeneration());

        return Double.compare(compatibility, compatibilityThreshold) < 0;
    }

    public void setMostCompatibleSpecies(final Species species) {
        minimumCompatibility = Double.POSITIVE_INFINITY;
        mostCompatibleSpecies = species;
    }

    public float getFitness() {
        return fitnessDeterminer.get();
    }

    public float updateFitness() {
        if (fitnessGeneration != population.getGeneration()) {
            fitnessGeneration = population.getGeneration();
            fitnessDeterminer.clear();
        }

        float fitness = context.general().calculateFitness(genome);
        float fitnessFixed = Float.isFinite(fitness) ? Math.max(fitness, 0f) : 0f;

        fitnessDeterminer.add(fitnessFixed);

        return fitnessDeterminer.get();
    }

    public void mutate() {
        genome.mutate();
    }

    @Override
    public int compareTo(final Organism other) {
        return Float.compare(fitnessDeterminer.get(), other.fitnessDeterminer.get());
    }

    public Organism mate(final Organism other) {
        int comparison = compareTo(other);

        GenomeDefault genomeNew = switch (comparison) {
            case 1 -> context.crossOver().crossOverBySkippingUnfitDisjointOrExcess(genome, other.genome);

            case 0 -> context.crossOver().crossOverByEqualTreatment(genome, other.genome);

            default -> context.crossOver().crossOverBySkippingUnfitDisjointOrExcess(other.genome, genome);
        };

        return new Organism(context, population, genomeNew);
    }

    public void freeze() {
        genome.freeze();
    }

    public float[] activate(final float[] inputs) {
        return genome.activate(inputs);
    }

    public Organism createCopy() {
        return new Organism(context, population, genome.createCopy());
    }

    public Organism createClone() {
        return new Organism(context, population, genome.createClone());
    }

    public void kill() {
        context.general().markToKill(genome);
    }
}