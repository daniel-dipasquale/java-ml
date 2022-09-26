package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.rl.neat.NeatContext;

@FunctionalInterface
public interface GenesisGenomeConnector {
    void setupConnections(Genome genome, NeatContext.ConnectionGeneSupport connectionGeneSupport);
}
