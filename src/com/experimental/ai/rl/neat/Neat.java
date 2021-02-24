package com.experimental.ai.rl.neat;

final class Neat<T extends Comparable<T>> {
    private final Context<T> context;
    private final Population<T> population;

    Neat(final Context<T> context) {
        this.context = context;
        this.population = new Population<>(context);
    }
}
