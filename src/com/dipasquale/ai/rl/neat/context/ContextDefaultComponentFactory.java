package com.dipasquale.ai.rl.neat.context;

@FunctionalInterface
public interface ContextDefaultComponentFactory<T> {
    T create(ContextDefault context);
}
