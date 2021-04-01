package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.species.Species;
import com.dipasquale.data.structure.deque.Node;
import com.dipasquale.data.structure.deque.NodeDeque;

@FunctionalInterface
interface SpeciesFitnessStrategy {
    <T extends Node> void process(NodeDeque<Species, T> species);
}
