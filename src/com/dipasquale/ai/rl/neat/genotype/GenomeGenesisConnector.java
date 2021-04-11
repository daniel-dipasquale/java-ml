package com.dipasquale.ai.rl.neat.genotype;

import java.io.Serializable;

@FunctionalInterface
public interface GenomeGenesisConnector extends Serializable {
    void setupConnections(GenomeDefault genome, GenomeHistoricalMarkings historicalMarkings);
}
