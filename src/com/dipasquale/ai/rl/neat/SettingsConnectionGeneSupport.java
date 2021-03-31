package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.common.SequentialIdFactoryLong;
import com.dipasquale.ai.rl.neat.context.ContextDefaultComponentFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultConnectionGeneSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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

    ContextDefaultComponentFactory<ContextDefaultConnectionGeneSupport> createFactory(final SettingsNeuralNetworkSupport neuralNetwork) {
        return context -> {
            boolean multipleRecurrentCyclesAllowed = neuralNetwork.getType() == SettingsNeuralNetworkType.MULTI_CYCLE_RECURRENT;
            SequentialIdFactory sequentialIdFactory = new SequentialIdFactoryDefault("innovation-id", innovationIdFactory);

            return new ContextDefaultConnectionGeneSupport(multipleRecurrentCyclesAllowed, sequentialIdFactory, new ConcurrentHashMap<>(), weightFactory::get, w -> weightPerturber.get() * w);
        };
    }
}
