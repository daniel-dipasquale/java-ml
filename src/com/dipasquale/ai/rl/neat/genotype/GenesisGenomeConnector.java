package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.rl.neat.Context;

@FunctionalInterface
public interface GenesisGenomeConnector {
    void setupConnections(Genome genome, Context.ConnectionGeneSupport connectionGeneSupport);
}
