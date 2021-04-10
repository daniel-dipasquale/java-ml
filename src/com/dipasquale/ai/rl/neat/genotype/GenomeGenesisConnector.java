package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.rl.neat.context.HistoricalMarkings;

import java.io.Serializable;

@FunctionalInterface
public interface GenomeGenesisConnector extends Serializable {
    void connect(GenomeDefault genome, HistoricalMarkings historicalMarkings);
}
