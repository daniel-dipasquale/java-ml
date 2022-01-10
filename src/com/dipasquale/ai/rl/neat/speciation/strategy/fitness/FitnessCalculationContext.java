package com.dipasquale.ai.rl.neat.speciation.strategy.fitness;

import com.dipasquale.ai.rl.neat.core.Context;
import com.dipasquale.ai.rl.neat.speciation.core.Species;
import com.dipasquale.data.structure.deque.NodeDeque;
import com.dipasquale.data.structure.deque.SimpleNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class FitnessCalculationContext {
    private final Context parent;
    private final NodeDeque<Species, SimpleNode<Species>> speciesNodes;
}
