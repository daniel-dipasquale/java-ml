package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.common.SequentialIdFactoryLong;
import com.dipasquale.ai.rl.neat.context.ContextDefaultComponentFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultConnectionGeneSupport;
import com.dipasquale.ai.rl.neat.genotype.DirectedEdge;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class SettingsConnectionGeneSupport {
    @Builder.Default
    private final SequentialIdFactory innovationIdFactory = new SequentialIdFactoryLong();
    @Builder.Default
    private final SettingsFloatNumber weightFactory = SettingsFloatNumber.randomMeanDistribution(-2f, 2f);
    @Builder.Default
    private final SettingsFloatNumber weightPerturber = SettingsFloatNumber.randomMeanDistribution(0f, 1f);

    ContextDefaultComponentFactory<ContextDefaultConnectionGeneSupport> createFactory(final SettingsNeuralNetworkSupport neuralNetwork, final SettingsParallelism parallelism) {
        return context -> {
            boolean multipleRecurrentCyclesAllowed = neuralNetwork.getType() == SettingsNeuralNetworkType.MULTI_CYCLE_RECURRENT;
            SequentialIdFactory sequentialIdFactory = new SequentialIdFactorySynchronized("innovation-id", innovationIdFactory);

            Map<DirectedEdge, InnovationId> innovationIds = !parallelism.isEnabled()
                    ? new HashMap<>()
                    : new ConcurrentHashMap<>(16, 0.75f, parallelism.getNumberOfThreads());

            return new ContextDefaultConnectionGeneSupport(multipleRecurrentCyclesAllowed, sequentialIdFactory, innovationIds, weightFactory::get, w -> weightPerturber.get() * w);
        };
    }
}
