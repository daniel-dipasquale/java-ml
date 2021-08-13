/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.genotype;

@FunctionalInterface
public interface GenomeGenesisConnector {
    void setupConnections(DefaultGenome genome, GenomeHistoricalMarkings historicalMarkings);
}
