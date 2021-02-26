package com.dipasquale.ai.rl.neat;

final class NeatDefault<T extends Comparable<T>> implements Neat {
    private final Context<T> context;
    private final Population<T> population;

    NeatDefault(final Context<T> context) {
        this.context = context;
        this.population = new Population<>(context);
    }
}
