package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.species.Species;
import com.dipasquale.data.structure.deque.Node;
import com.dipasquale.data.structure.deque.NodeDeque;

final class SpeciesFitnessStrategyDefault implements SpeciesFitnessStrategy {
    @Override
    public <T extends Node> void process(final NodeDeque<Species, T> species) {
        for (T speciesNode : species) {
            species.getValue(speciesNode).updateFitness();
        }
    }
}
