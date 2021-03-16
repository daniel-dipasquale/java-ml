package com.dipasquale.ai.rl.neat;

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
