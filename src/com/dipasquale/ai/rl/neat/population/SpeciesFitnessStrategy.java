package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.genotype.Species;
import com.dipasquale.data.structure.deque.Node;
import com.dipasquale.data.structure.deque.NodeDeque;

import java.io.Serializable;

@FunctionalInterface
interface SpeciesFitnessStrategy extends Serializable {
    <T extends Node> void process(NodeDeque<Species, T> speciesNodes);
}
