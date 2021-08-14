package com.dipasquale.ai.rl.neat.genotype;

@FunctionalInterface
public interface GenomeGenesisConnector {
    void setupConnections(DefaultGenome genome, GenomeHistoricalMarkings historicalMarkings);
}
