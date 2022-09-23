package com.dipasquale.ai.rl.neat.speciation.strategy.fitness;

import com.dipasquale.ai.rl.neat.speciation.Species;
import com.dipasquale.data.structure.deque.StandardNode;
import com.dipasquale.synchronization.InterruptedRuntimeException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class AllFitnessEvaluationStrategy implements FitnessEvaluationStrategy {
    @Override
    public void calculate(final FitnessEvaluationContext context) {
        for (StandardNode<Species> speciesNode : context.getSpeciesNodes()) {
            Species species = context.getSpeciesNodes().getValue(speciesNode);

            species.updateAllFitness(context.getParent());
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
