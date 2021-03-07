package com.dipasquale.ai.rl.neat;

final class Organism implements Comparable<Organism> {
    private final Context context;
    private final Population population;
    private final GenomeDefault genome;
    private final FitnessDeterminer fitnessDeterminer;
    private int generation;

    Organism(final Context context, final Population population, final GenomeDefault genome) {
        this.context = context;
        this.population = population;
        this.genome = genome;
        this.fitnessDeterminer = context.general().createFitnessDeterminer();
        this.generation = -1;
    }

    public float updateFitness() {
        if (generation != population.getGeneration()) {
            generation = population.getGeneration();
            fitnessDeterminer.clear();
        }

        float fitness = Math.max(context.general().calculateFitness(genome), Float.MIN_VALUE);

        fitnessDeterminer.add(fitness);

        return fitnessDeterminer.get();
    }

    public boolean isCompatible(final Organism other) {
        return context.speciation().belongs(genome, other.genome, population.getGeneration());
    }

    public void mutate() {
        genome.mutate();
    }

    public Organism mate(final Organism other) {
        int comparison = Float.compare(fitnessDeterminer.get(), other.fitnessDeterminer.get());

        GenomeDefault genomeNew = switch (comparison) {
            case 1 -> context.crossOver().crossOverBySkippingUnfitDisjointOrExcess(genome, other.genome);
            case 0 -> context.crossOver().crossOverByEqualTreatment(genome, other.genome);
            default -> context.crossOver().crossOverBySkippingUnfitDisjointOrExcess(other.genome, genome);
        };

        return new Organism(context, population, genomeNew);
    }

    public Organism createCopy() {
        return new Organism(context, population, genome.createCopy());
    }

    @Override
    public int compareTo(final Organism other) {
        return Float.compare(fitnessDeterminer.get(), other.fitnessDeterminer.get());
    }
}