package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.data.structure.deque.Node;
import com.dipasquale.data.structure.deque.NodeDeque;

final class SpeciesFitnessStrategyUpdateSpecies implements SpeciesFitnessStrategy {
    @Override
    public <T extends Node> void process(final NodeDeque<Species, T> speciesNodes) {
        for (T speciesNode : speciesNodes) {
            speciesNodes.getValue(speciesNode).updateSharedFitness();
        }
    }
}
