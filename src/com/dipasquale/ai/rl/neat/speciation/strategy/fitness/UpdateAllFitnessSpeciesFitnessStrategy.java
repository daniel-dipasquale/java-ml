package com.dipasquale.ai.rl.neat.speciation.strategy.fitness;

import com.dipasquale.ai.rl.neat.speciation.core.Species;
import com.dipasquale.data.structure.deque.SimpleNode;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class UpdateAllFitnessSpeciesFitnessStrategy implements SpeciesFitnessStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = 9126364579807820036L;

    @Override
    public void update(final SpeciesFitnessContext context) {
        for (SimpleNode<Species> speciesNode : context.getSpeciesNodes()) {
            Species species = context.getSpeciesNodes().getValue(speciesNode);

            species.updateAllFitness(context.getParent());
        }
    }
}
