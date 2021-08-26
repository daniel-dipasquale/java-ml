package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.rl.neat.context.Context;

@FunctionalInterface
public interface GenesisGenomeConnector {
    void setupConnections(DefaultGenome genome, Context.ConnectionGeneSupport connections);
}
