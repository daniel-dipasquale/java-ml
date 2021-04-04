package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.common.SequentialIdFactoryDefault;
import com.dipasquale.ai.rl.neat.context.ConnectionGeneWeightFactory;
import com.dipasquale.ai.rl.neat.context.ConnectionGeneWeightPerturber;
import com.dipasquale.ai.rl.neat.context.ContextDefaultComponentFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultConnectionGeneSupport;
import com.dipasquale.ai.rl.neat.genotype.DirectedEdge;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.common.FloatFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SettingsConnectionGeneSupport {
    @Builder.Default
    private final SettingsFloatNumber weightFactory = SettingsFloatNumber.random(SettingsRandomType.UNIFORM, -1f, 1f);
    @Builder.Default
    private final SettingsFloatNumber weightPerturber = SettingsFloatNumber.literal(2.5f);

    ConnectionGeneWeightFactory createWeightFactory(final SettingsParallelism parallelism) {
        return new ConnectionGeneWeightFactoryDefault(weightFactory.createFactory(parallelism));
    }

    ConnectionGeneWeightPerturber createWeightPerturber(final SettingsParallelism parallelism) {
        return new ConnectionGeneWeightPerturberDefault(weightPerturber.createFactory(parallelism));
    }

    ContextDefaultComponentFactory<ContextDefaultConnectionGeneSupport> createFactory(final SettingsNeuralNetworkSupport neuralNetwork, final SettingsParallelism parallelism) {
        return context -> {
            boolean multipleRecurrentCyclesAllowed = neuralNetwork.getType() == SettingsNeuralNetworkType.MULTI_CYCLE_RECURRENT;
            SequentialIdFactory innovationIdFactory = parallelism.createSequentialIdFactory("innovation-id", new SequentialIdFactoryDefault());

            Map<DirectedEdge, InnovationId> innovationIds = !parallelism.isEnabled()
                    ? new HashMap<>()
                    : new ConcurrentHashMap<>(16, 0.75f, parallelism.getNumberOfThreads());

            ConnectionGeneWeightFactory weightFactoryFixed = createWeightFactory(parallelism);
            ConnectionGeneWeightPerturber weightPerturberFixed = createWeightPerturber(parallelism);

            return new ContextDefaultConnectionGeneSupport(multipleRecurrentCyclesAllowed, innovationIdFactory, innovationIds, weightFactoryFixed, weightPerturberFixed);
        };
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class ConnectionGeneWeightFactoryDefault implements ConnectionGeneWeightFactory {
        private final FloatFactory weightFactory;

        @Override
        public float next() {
            return weightFactory.create();
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class ConnectionGeneWeightPerturberDefault implements ConnectionGeneWeightPerturber {
        private final FloatFactory weightPerturber;

        @Override
        public float next(final float value) {
            return weightPerturber.create() * value;
        }
    }
}
