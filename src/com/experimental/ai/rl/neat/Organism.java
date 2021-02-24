package com.experimental.ai.rl.neat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class Organism<T extends Comparable<T>> {
    private final Context.GeneralSupport<T> generalSupport;
    @Getter
    private final GenomeDefault<T> genome;
    @Getter
    private float fitness = 0f;

    public float updateFitness() {
        fitness = Math.max(generalSupport.calculateFitness(genome), Float.MIN_VALUE);

        return fitness;
    }
}