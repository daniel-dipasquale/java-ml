package com.dipasquale.ai.rl.neat.speciation.strategy.fitness;

import com.dipasquale.ai.rl.neat.speciation.core.Species;
import com.dipasquale.data.structure.deque.Node;
import com.dipasquale.data.structure.deque.NodeDeque;

@FunctionalInterface
public interface SpeciesFitnessStrategy {
    <T extends Node> void process(NodeDeque<Species, T> speciesNodes);
}
