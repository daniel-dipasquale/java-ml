package com.dipasquale.ai.rl.neat.speciation;

import com.dipasquale.data.structure.deque.Node;
import com.dipasquale.data.structure.deque.NodeDeque;

@FunctionalInterface
interface SpeciesFitnessStrategy {
    <T extends Node> void process(NodeDeque<Species, T> speciesNodes);
}
