package com.dipasquale.ai.rl.neat;

@FunctionalInterface
public interface SharedNeatEnvironment extends NeatEnvironment {
    void test(SharedGenomeActivator sharedGenomeActivator);
}
