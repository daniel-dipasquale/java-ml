package com.dipasquale.ai.rl.neat.core;

@FunctionalInterface
public interface SharedNeatEnvironment extends NeatEnvironment {
    void test(SharedGenomeActivator sharedGenomeActivator);
}
