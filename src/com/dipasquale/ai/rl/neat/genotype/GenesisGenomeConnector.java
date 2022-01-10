package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.rl.neat.core.Context;

@FunctionalInterface
public interface GenesisGenomeConnector {
    void setupConnections(Genome genome, Context.ConnectionGeneSupport connectionGeneSupport);
}
