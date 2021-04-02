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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SettingsConnectionGeneSupport {
    @Builder.Default
    private final SettingsFloatNumber weightFactory = SettingsFloatNumber.randomMeanDistribution(-1f, 1f);
    @Builder.Default
    private final SettingsFloatNumber weightPerturber = SettingsFloatNumber.randomMeanDistribution(0f, 1f);

    ContextDefaultComponentFactory<ContextDefaultConnectionGeneSupport> createFactory(final SettingsNeuralNetworkSupport neuralNetwork, final SettingsParallelism parallelism) {
        return context -> {
            boolean multipleRecurrentCyclesAllowed = neuralNetwork.getType() == SettingsNeuralNetworkType.MULTI_CYCLE_RECURRENT;
            SequentialIdFactory innovationIdFactory = new SequentialIdFactoryLong();
            SequentialIdFactory innovationIdFactoryFixed = new SequentialIdFactorySynchronized("innovation-id", innovationIdFactory);

            Map<DirectedEdge, InnovationId> innovationIds = !parallelism.isEnabled()
                    ? new HashMap<>()
                    : new ConcurrentHashMap<>(16, 0.75f, parallelism.getNumberOfThreads());

            return new ContextDefaultConnectionGeneSupport(multipleRecurrentCyclesAllowed, innovationIdFactoryFixed, innovationIds, weightFactory::get, w -> weightPerturber.get() * w);
        };
    }
}
