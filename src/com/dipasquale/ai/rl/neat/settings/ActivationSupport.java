package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.common.fitness.FitnessDeterminerFactory;
import com.dipasquale.ai.rl.neat.common.FitnessBucket;
import com.dipasquale.ai.rl.neat.context.DefaultContextActivationSupport;
import com.dipasquale.ai.rl.neat.core.NeatEnvironment;
import com.dipasquale.ai.rl.neat.phenotype.FeedForwardNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.RecurrentNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.phenotype.DualModeGenomeActivatorPool;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter(AccessLevel.PACKAGE)
public final class ActivationSupport {
    @Builder.Default
    private final NeuralNetworkType neuralNetworkType = NeuralNetworkType.MULTI_CYCLE_RECURRENT;

    DefaultContextActivationSupport create(final GeneralEvaluatorSupport general, final ParallelismSupport parallelism) {
        NeuralNetworkFactory neuralNetworkFactory = switch (neuralNetworkType) {
            case FEED_FORWARD -> new FeedForwardNeuralNetworkFactory();

            default -> new RecurrentNeuralNetworkFactory();
        };

        DualModeGenomeActivatorPool genomeActivatorPool = new DualModeGenomeActivatorPool(parallelism.isEnabled(), parallelism.getNumberOfThreads(), neuralNetworkFactory);
        NeatEnvironment neatEnvironment = general.getFitnessFunction();
        FitnessDeterminerFactory fitnessDeterminerFactory = general.getFitnessDeterminerFactory();
        DualModeMap<String, FitnessBucket> fitnessBuckets = new DualModeMap<>(parallelism.isEnabled(), parallelism.getNumberOfThreads());

        return new DefaultContextActivationSupport(genomeActivatorPool, neatEnvironment, fitnessDeterminerFactory, fitnessBuckets);
    }
}
