package com.dipasquale.ai.rl.neat;

@FunctionalInterface
public interface CommunalNeatEnvironment extends NeatEnvironment {
    void test(CommunalGenomeActivator communalGenomeActivator);
}
