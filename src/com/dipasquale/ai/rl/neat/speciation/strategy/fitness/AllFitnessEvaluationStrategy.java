package com.dipasquale.ai.rl.neat.speciation.strategy.fitness;

import com.dipasquale.ai.rl.neat.NeatContext;
import com.dipasquale.ai.rl.neat.speciation.Species;
import com.dipasquale.data.structure.deque.NodeDeque;
import com.dipasquale.data.structure.deque.StandardNode;
import com.dipasquale.synchronization.InterruptedRuntimeException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AllFitnessEvaluationStrategy implements FitnessEvaluationStrategy {
    private static final AllFitnessEvaluationStrategy INSTANCE = new AllFitnessEvaluationStrategy();

    public static AllFitnessEvaluationStrategy getInstance() {
        return INSTANCE;
    }

    @Override
    public void evaluate(final FitnessEvaluationContext context) {
        NodeDeque<Species, StandardNode<Species>> speciesNodes = context.getSpeciesNodes();
        NeatContext parentContext = context.getParent();

        for (StandardNode<Species> speciesNode : speciesNodes) {
            Species species = speciesNodes.getValue(speciesNode);

            species.updateAllFitness(parentContext);
        }

        if (Thread.interrupted()) {
            String message = "thread was interrupted while updating all organisms and species fitness";

            try {
                throw new InterruptedException(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                throw new InterruptedRuntimeException(message, e);
            }
        }
    }
}
