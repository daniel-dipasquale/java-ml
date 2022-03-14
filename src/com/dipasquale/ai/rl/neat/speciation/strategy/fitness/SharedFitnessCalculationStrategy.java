package com.dipasquale.ai.rl.neat.speciation.strategy.fitness;

import com.dipasquale.ai.rl.neat.Context;
import com.dipasquale.ai.rl.neat.speciation.Species;
import com.dipasquale.data.structure.deque.PlainNode;

import java.io.Serial;
import java.io.Serializable;

public final class SharedFitnessCalculationStrategy implements FitnessCalculationStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = -5870473327936844508L;

    @Override
    public void calculate(final FitnessCalculationContext context) {
        Context.MetricsSupport metricsSupport = context.getParent().metrics();

        for (PlainNode<Species> speciesNode : context.getSpeciesNodes()) {
            Species species = context.getSpeciesNodes().getValue(speciesNode);

            species.updateSharedFitnessOnly(metricsSupport);
        }
    }
}
