package com.dipasquale.ai.rl.neat.speciation;

import com.dipasquale.data.structure.deque.Node;
import com.dipasquale.data.structure.deque.NodeDeque;

import java.io.Serial;
import java.io.Serializable;

final class SpeciesFitnessStrategyUpdateSpecies implements SpeciesFitnessStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = -5870473327936844508L;

    @Override
    public <T extends Node> void process(final NodeDeque<Species, T> speciesNodes) {
        for (T speciesNode : speciesNodes) {
            speciesNodes.getValue(speciesNode).updateSharedFitness();
        }
    }
}
