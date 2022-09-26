package com.dipasquale.ai.rl.neat.factory;

import com.dipasquale.ai.common.fitness.FitnessBucket;
import com.dipasquale.ai.common.fitness.FitnessControllerFactory;
import com.dipasquale.common.factory.ObjectIndexProvider;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class FitnessBucketProvider implements ObjectIndexProvider<FitnessBucket>, Serializable {
    @Serial
    private static final long serialVersionUID = 6526661541803137111L;
    private final FitnessControllerFactory factory;

    @Override
    public FitnessBucket provide(final int index) {
        return new FitnessBucket(factory.create());
    }
}
