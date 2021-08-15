package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.fitness.FitnessFunction;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.threading.event.loop.IterableEventLoop;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class DefaultContextStateOverrideSupport implements Context.StateOverrideSupport {
    private final FitnessFunction<Genome> fitnessFunction;
    private final IterableEventLoop eventLoop;

    @Override
    public FitnessFunction<Genome> fitnessFunction() {
        return fitnessFunction;
    }

    @Override
    public IterableEventLoop eventLoop() {
        return eventLoop;
    }
}
