package com.dipasquale.ai.rl.neat.speciation.strategy.fitness;

import com.dipasquale.ai.rl.neat.Context;
import com.dipasquale.ai.rl.neat.speciation.Species;
import com.dipasquale.data.structure.deque.StandardNode;

public final class SharedFitnessEvaluationStrategy implements FitnessEvaluationStrategy {
    @Override
    public void calculate(final FitnessEvaluationContext context) {
        Context.MetricsSupport metricsSupport = context.getParent().metrics();

        for (StandardNode<Species> speciesNode : context.getSpeciesNodes()) {
            Species species = context.getSpeciesNodes().getValue(speciesNode);

            species.updateSharedFitnessOnly(metricsSupport);
        }
    }
}
