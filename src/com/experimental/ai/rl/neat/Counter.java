package com.experimental.ai.rl.neat;

public interface Counter<T extends Comparable<T>> {
    T next();

    T current();
}
