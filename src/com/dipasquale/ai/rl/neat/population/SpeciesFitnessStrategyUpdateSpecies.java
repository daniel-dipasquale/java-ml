package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.genotype.Species;
import com.dipasquale.data.structure.deque.Node;
import com.dipasquale.data.structure.deque.NodeDeque;

import java.io.Serial;

final class SpeciesFitnessStrategyUpdateSpecies implements SpeciesFitnessStrategy {
    @Serial
    private static final long serialVersionUID = 5009349469775909264L;

    @Override
    public <T extends Node> void process(final NodeDeque<Species, T> speciesNodes) {
        for (T speciesNode : speciesNodes) {
            speciesNodes.getValue(speciesNode).updateSharedFitness();
        }
    }
}
