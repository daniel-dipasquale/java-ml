package com.dipasquale.ai.rl.neat.speciation.strategy.fitness;

import com.dipasquale.ai.rl.neat.NeatContext;
import com.dipasquale.ai.rl.neat.speciation.Species;
import com.dipasquale.data.structure.deque.NodeDeque;
import com.dipasquale.data.structure.deque.StandardNode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpeciesFitnessEvaluationStrategy implements FitnessEvaluationStrategy {
    private static final SpeciesFitnessEvaluationStrategy INSTANCE = new SpeciesFitnessEvaluationStrategy();

    public static SpeciesFitnessEvaluationStrategy getInstance() {
        return INSTANCE;
    }

    @Override
    public void evaluate(final FitnessEvaluationContext context) {
        NodeDeque<Species, StandardNode<Species>> speciesNodes = context.getSpeciesNodes();
        NeatContext.MetricsSupport metricsSupport = context.getParent().getMetrics();

        for (StandardNode<Species> speciesNode : speciesNodes) {
            Species species = speciesNodes.getValue(speciesNode);

            species.updateSharedFitnessOnly(metricsSupport);
        }
    }
}
