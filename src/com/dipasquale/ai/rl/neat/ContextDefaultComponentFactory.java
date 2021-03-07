package com.dipasquale.ai.rl.neat;

@FunctionalInterface
interface ContextDefaultComponentFactory<T> {
    T create(ContextDefault context);
}
