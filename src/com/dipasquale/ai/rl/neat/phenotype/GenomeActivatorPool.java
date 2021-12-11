package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.speciation.core.PopulationState;

public interface GenomeActivatorPool {
    GenomeActivator provide(Genome genome, PopulationState populationState);

    GenomeActivator create(Genome genome, PopulationState populationState);
}
