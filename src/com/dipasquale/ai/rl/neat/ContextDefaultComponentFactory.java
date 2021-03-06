package com.dipasquale.ai.rl.neat;

@FunctionalInterface
interface ContextDefaultComponentFactory<T extends Comparable<T>, R> {
    R create(ContextDefault<T> context);
}
