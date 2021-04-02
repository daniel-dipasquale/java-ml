package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.Organism;
import com.dipasquale.ai.rl.neat.species.Species;
import com.dipasquale.data.structure.deque.Node;
import com.dipasquale.data.structure.deque.NodeDeque;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciesFitnessStrategyUpdateOrganisms implements SpeciesFitnessStrategy {
    private final Context context;

    @Override
    public <T extends Node> void process(final NodeDeque<Species, T> speciesNodes) {
        Stream<Organism> organisms = speciesNodes.stream()
                .map(speciesNodes::getValue)
                .flatMap(s -> s.getOrganisms().stream());

        context.parallelism().forEach(organisms, Organism::updateFitness);

        try {
            context.parallelism().waitUntilDone();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new RuntimeException("thread was interrupted while updating the organisms fitness", e);
        }
    }
}
