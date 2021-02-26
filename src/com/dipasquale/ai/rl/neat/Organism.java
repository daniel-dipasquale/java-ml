package com.dipasquale.ai.rl.neat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class Organism<T extends Comparable<T>> {
    private final Context<T> context;
    private final Population<T> population;
    private final GenomeDefault<T> genome;
    @Getter
    private float fitness = 0f;

    public float updateFitness() {
        fitness = Math.max(context.general().calculateFitness(genome), Float.MIN_VALUE);

        return fitness;
    }

    public boolean isCompatible(final Organism<T> other) {
        return context.speciation().belongs(genome, other.genome, population.getGeneration());
    }

    public void mutate() {
        genome.mutate();
    }

    public Organism<T> mate(final Organism<T> other) {
        if (Float.compare(fitness, other.fitness) >= 0) {
            return new Organism<>(context, population, GenomeDefault.crossover(genome, other.genome));
        }

        return new Organism<>(context, population, GenomeDefault.crossover(other.genome, genome));
    }

    public Organism<T> createCopy() {
        return new Organism<>(context, population, genome.createCopy());
    }
}