package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.data.structure.deque.Node;
import com.dipasquale.data.structure.deque.NodeDeque;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciesFitnessStrategyDefault implements SpeciesFitnessStrategy {
    private final Context.GeneralSupport general;

    @Override
    public <T extends Node> void process(final NodeDeque<Species, T> speciesNodes) {
        for (T speciesNode : speciesNodes) {
            speciesNodes.getValue(speciesNode).updateFitness(general);
        }
    }
}
