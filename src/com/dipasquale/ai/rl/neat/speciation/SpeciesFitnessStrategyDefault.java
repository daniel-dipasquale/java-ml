package com.dipasquale.ai.rl.neat.speciation;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.data.structure.deque.Node;
import com.dipasquale.data.structure.deque.NodeDeque;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciesFitnessStrategyDefault implements SpeciesFitnessStrategy {
    @Serial
    private static final long serialVersionUID = 8618927004394952964L;
    private final Context.GeneralSupport general;

    @Override
    public <T extends Node> void process(final NodeDeque<Species, T> speciesNodes) {
        for (T speciesNode : speciesNodes) {
            speciesNodes.getValue(speciesNode).updateFitness(general);
        }
    }
}
