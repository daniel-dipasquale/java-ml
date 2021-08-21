package com.dipasquale.ai.rl.neat.speciation.strategy.fitness;

import com.dipasquale.ai.rl.neat.speciation.core.Species;
import com.dipasquale.data.structure.deque.SimpleNode;

import java.io.Serial;
import java.io.Serializable;

public final class UpdateSharedSpeciesFitnessStrategy implements SpeciesFitnessStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = -5870473327936844508L;

    @Override
    public void update(final SpeciesFitnessContext context) {
        for (SimpleNode<Species> speciesNode : context.getSpeciesNodes()) {
            Species species = context.getSpeciesNodes().getValue(speciesNode);

            species.updateSharedFitnessOnly();
        }
    }
}
