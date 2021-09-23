package com.dipasquale.ai.rl.neat.speciation.strategy.fitness;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.synchronization.wait.handle.WaitHandle;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Iterator;

@RequiredArgsConstructor
public final class ParallelUpdateSpeciesFitnessStrategy implements SpeciesFitnessStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = 5632936446515400703L;

    @Override
    public void update(final SpeciesFitnessContext context) {
        Iterator<Organism> organisms = context.getSpeciesNodes().stream()
                .map(context.getSpeciesNodes()::getValue)
                .flatMap(s -> s.getOrganisms().stream())
                .iterator();

        Context.ActivationSupport neuralNetwork = context.getParent().neuralNetwork();
        WaitHandle waitHandle = context.getParent().parallelism().forEach(organisms, o -> o.updateFitness(neuralNetwork));

        try {
            waitHandle.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new RuntimeException("thread was interrupted while updating the organisms fitness", e);
        }
    }
}
