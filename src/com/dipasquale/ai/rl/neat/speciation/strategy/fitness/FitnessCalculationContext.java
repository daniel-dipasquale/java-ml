package com.dipasquale.ai.rl.neat.speciation.strategy.fitness;

import com.dipasquale.ai.rl.neat.Context;
import com.dipasquale.ai.rl.neat.speciation.Species;
import com.dipasquale.data.structure.deque.NodeDeque;
import com.dipasquale.data.structure.deque.PlainNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class FitnessCalculationContext {
    private final Context parent;
    private final NodeDeque<Species, PlainNode<Species>> speciesNodes;
}
