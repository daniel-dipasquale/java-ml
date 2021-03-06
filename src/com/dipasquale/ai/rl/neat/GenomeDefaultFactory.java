package com.dipasquale.ai.rl.neat;

@FunctionalInterface
interface GenomeDefaultFactory<T extends Comparable<T>> {
    GenomeDefault<T> create();
}
