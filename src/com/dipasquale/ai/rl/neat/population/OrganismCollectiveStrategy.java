package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.NeatCollectiveClient;
import com.dipasquale.ai.rl.neat.genotype.Organism;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
final class OrganismCollectiveStrategy implements NeatCollectiveClient {
    private Organism organism;

    @Override
    public float[] activate(final float[] inputs) {
        return organism.activate(inputs);
    }
}
