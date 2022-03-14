package com.dipasquale.ai.rl.neat.speciation.strategy.fitness;

import com.dipasquale.ai.rl.neat.speciation.Species;
import com.dipasquale.data.structure.deque.PlainNode;
import com.dipasquale.synchronization.InterruptedRuntimeException;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class AllFitnessCalculationStrategy implements FitnessCalculationStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = 9126364579807820036L;

    @Override
    public void calculate(final FitnessCalculationContext context) {
        for (PlainNode<Species> speciesNode : context.getSpeciesNodes()) {
            Species species = context.getSpeciesNodes().getValue(speciesNode);

            species.updateAllFitness(context.getParent());
        }

        if (Thread.interrupted()) {
            String message = "thread was interrupted while updating all organisms and species fitness";

            try {
                throw new InterruptedException(message);
            } catch (InterruptedException e) {
                throw new InterruptedRuntimeException(message, e);
            }
        }
    }
}
