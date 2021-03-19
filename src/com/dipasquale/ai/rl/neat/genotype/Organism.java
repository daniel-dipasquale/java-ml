package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.FitnessDeterminer;
import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.population.Population;

public final class Organism implements Comparable<Organism> {
    private final Context context;
    private final Population population;
    private final GenomeDefault genome;
    private final FitnessDeterminer fitnessDeterminer;
    private int fitnessGeneration;

    public Organism(final Context context, final Population population, final GenomeDefault genome) {
        this.context = context;
        this.population = population;
        this.genome = genome;
        this.fitnessDeterminer = context.general().createFitnessDeterminer();
        this.fitnessGeneration = -1;
    }

    public float updateFitness() {
        if (fitnessGeneration != population.getGeneration()) {
            fitnessGeneration = population.getGeneration();
            fitnessDeterminer.clear();
        }

        float fitness = Math.max(context.general().calculateFitness(genome), 0f); // TODO: rely on a configurable function

        fitnessDeterminer.add(fitness);

        return fitnessDeterminer.get();
    }

    public boolean isCompatible(final Organism other) {
        return context.speciation().belongs(genome, other.genome, population.getGeneration());
    }

    public void mutate() {
        genome.mutate();
        genome.freeze();
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

        genomeNew.freeze();

        return new Organism(context, population, genomeNew);
    }

    public float[] activate(final float[] inputs) {
        return genome.activate(inputs);
    }

    public Organism createCopy() {
        return new Organism(context, population, genome.createCopy());
    }
}