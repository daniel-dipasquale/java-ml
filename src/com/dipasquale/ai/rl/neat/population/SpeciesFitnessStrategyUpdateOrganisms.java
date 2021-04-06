package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.Organism;
import com.dipasquale.data.structure.deque.Node;
import com.dipasquale.data.structure.deque.NodeDeque;
import com.dipasquale.threading.wait.handle.WaitHandle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciesFitnessStrategyUpdateOrganisms implements SpeciesFitnessStrategy {
    private final Context context;

    @Override
    public <T extends Node> void process(final NodeDeque<Species, T> speciesNodes) {
        Iterator<Organism> organisms = speciesNodes.stream()
                .map(speciesNodes::getValue)
                .flatMap(s -> s.getOrganisms().stream())
                .iterator();

        Context.GeneralSupport general = context.general();
        WaitHandle waitHandle = context.parallelism().forEach(organisms, o -> o.updateFitness(general));

        try {
            waitHandle.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new RuntimeException("thread was interrupted while updating the organisms fitness", e);
        }
    }
}
