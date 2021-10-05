package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.speciation.core.PopulationState;

public interface GenomeActivatorPool {
    GenomeActivator getOrCreate(Genome genome, PopulationState populationState);

    GenomeActivator createTransient(Genome genome, PopulationState populationState);
}
