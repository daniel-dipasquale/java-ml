package com.dipasquale.ai.rl.neat.speciation.strategy.fitness;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.speciation.core.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.synchronization.wait.handle.WaitHandle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Iterator;

@RequiredArgsConstructor
public final class ParallelUpdateSpeciesFitnessStrategy implements SpeciesFitnessStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = 5632936446515400703L;

    private static void updateFitness(final OrganismClassification organismClassification, final Context context) {
        organismClassification.organism.updateFitness(organismClassification.species, context);
    }

    @Override
    public void update(final SpeciesFitnessContext context) {
        Iterator<OrganismClassification> organisms = context.getSpeciesNodes().flattenedStream()
                .flatMap(s -> s.getOrganisms().stream()
                        .map(o -> new OrganismClassification(o, s)))
                .iterator();

        WaitHandle waitHandle = context.getParent().parallelism().forEach(organisms, oc -> updateFitness(oc, context.getParent()));

        try {
            waitHandle.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new RuntimeException("thread was interrupted while updating the organisms fitness", e);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class OrganismClassification {
        private final Organism organism;
        private final Species species;
    }
}
