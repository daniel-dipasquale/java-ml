package com.dipasquale.ai.rl.neat;

final class Organism<T extends Comparable<T>> implements Comparable<Organism<T>> {
    private final Context<T> context;
    private final Population<T> population;
    private final GenomeDefault<T> genome;
    private final FitnessDeterminer fitnessDeterminer;
    private int generation;

    Organism(final Context<T> context, final Population<T> population, final GenomeDefault<T> genome) {
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

    public boolean isCompatible(final Organism<T> other) {
        return context.speciation().belongs(genome, other.genome, population.getGeneration());
    }

    public void mutate() {
        genome.mutate();
    }

    public Organism<T> mate(final Organism<T> other) {
        int comparison = Float.compare(fitnessDeterminer.get(), other.fitnessDeterminer.get());

        GenomeDefault<T> genomeNew = switch (comparison) {
            case 1 -> context.crossOver().crossOverBySkippingUnfitDisjointOrExcess(genome, other.genome);
            case 0 -> context.crossOver().crossOverByEqualTreatment(genome, other.genome);
            default -> context.crossOver().crossOverBySkippingUnfitDisjointOrExcess(other.genome, genome);
        };

        return new Organism<>(context, population, genomeNew);
    }

    public Organism<T> createCopy() {
        return new Organism<>(context, population, genome.createCopy());
    }

    @Override
    public int compareTo(final Organism<T> other) {
        return Float.compare(fitnessDeterminer.get(), other.fitnessDeterminer.get());
    }
}