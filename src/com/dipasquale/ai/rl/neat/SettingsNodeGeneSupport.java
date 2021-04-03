package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.ActivationFunctionFactory;
import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.common.SequentialIdFactoryLong;
import com.dipasquale.ai.rl.neat.context.ContextDefaultComponentFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultNodeGeneSupport;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.common.EnumFactory;
import com.dipasquale.common.FloatFactory;
import com.dipasquale.common.RandomSupportFloat;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SettingsNodeGeneSupport {
    private static final Map<SettingsActivationFunction, ActivationFunction> ACTIVATION_FUNCTIONS_MAP = ImmutableMap.<SettingsActivationFunction, ActivationFunction>builder()
            .put(SettingsActivationFunction.IDENTITY, ActivationFunction.IDENTITY)
            .put(SettingsActivationFunction.RE_LU, ActivationFunction.RE_LU)
            .put(SettingsActivationFunction.SIGMOID, ActivationFunction.SIGMOID)
            .build();

    private static final List<ActivationFunction> ACTIVATION_FUNCTIONS = ImmutableList.copyOf(ACTIVATION_FUNCTIONS_MAP.values());
    @Builder.Default
    private final SettingsFloatNumber hiddenBias = SettingsFloatNumber.random(SettingsRandomType.UNIFORM, -1f, 1f);
    @Builder.Default
    private final SettingsEnum<SettingsActivationFunction> hiddenActivationFunction = SettingsEnum.literal(SettingsActivationFunction.SIGMOID);

    private static ActivationFunctionFactory createActivationFunctionFactory(final EnumFactory<SettingsActivationFunction> activationFunctionFactory, final RandomSupportFloat randomSupport) {
        return new ActivationFunctionFactoryUnknown(activationFunctionFactory.create(), randomSupport);
    }

    private static ActivationFunctionFactory createActivationFunctionFactory(final EnumFactory<SettingsOutputActivationFunction> outputActivationFunctionFactory, final EnumFactory<SettingsActivationFunction> hiddenActivationFunctionFactory, final RandomSupportFloat randomSupport) {
        SettingsOutputActivationFunction outputActivationFunctionFixed = outputActivationFunctionFactory.create();

        return switch (outputActivationFunctionFixed) {
            case RANDOM -> new ActivationFunctionFactoryUnknown(SettingsActivationFunction.RANDOM, randomSupport);

            case IDENTITY -> new ActivationFunctionFactoryLiteral(SettingsActivationFunction.IDENTITY);

            case RE_LU -> new ActivationFunctionFactoryLiteral(SettingsActivationFunction.RE_LU);

            case SIGMOID -> new ActivationFunctionFactoryLiteral(SettingsActivationFunction.SIGMOID);

            case COPY_FROM_HIDDEN -> new ActivationFunctionFactoryUnknown(hiddenActivationFunctionFactory.create(), randomSupport);
        };
    }

    private static FloatFactory createBiasFactory(final SettingsGenomeFactory genomeFactory, final SettingsParallelism parallelism) {
        if (genomeFactory.getBiases().size() == 0) {
            return new FloatFactoryNoBias();
        }

        List<FloatFactory> biasNodeBiasFactories = genomeFactory.getBiases().stream()
                .map(sfn -> sfn.createFactory(parallelism))
                .collect(Collectors.toList());

        if (!parallelism.isEnabled()) {
            return new FloatFactoryBiasDefault(biasNodeBiasFactories);
        }

        return new FloatFactoryBiasConcurrent(biasNodeBiasFactories);
    }

    ContextDefaultComponentFactory<ContextDefaultNodeGeneSupport> createFactory(final SettingsGenomeFactory genomeFactory, final SettingsParallelism parallelism) {
        return context -> {
            Map<NodeGeneType, SequentialIdFactory> sequentialIdFactories = ImmutableMap.<NodeGeneType, SequentialIdFactory>builder()
                    .put(NodeGeneType.INPUT, parallelism.createSequentialIdFactory("n1_input", new SequentialIdFactoryLong()))
                    .put(NodeGeneType.OUTPUT, parallelism.createSequentialIdFactory("n4_output", new SequentialIdFactoryLong()))
                    .put(NodeGeneType.BIAS, parallelism.createSequentialIdFactory("n2_bias", new SequentialIdFactoryLong()))
                    .put(NodeGeneType.HIDDEN, parallelism.createSequentialIdFactory("n3_hidden", new SequentialIdFactoryLong()))
                    .build();

            Map<NodeGeneType, FloatFactory> biasFactories = ImmutableMap.<NodeGeneType, FloatFactory>builder()
                    .put(NodeGeneType.INPUT, new FloatFactoryStrategy(genomeFactory.getInputBias().createFactory(parallelism)))
                    .put(NodeGeneType.OUTPUT, new FloatFactoryStrategy(genomeFactory.getOutputBias().createFactory(parallelism)))
                    .put(NodeGeneType.BIAS, createBiasFactory(genomeFactory, parallelism))
                    .put(NodeGeneType.HIDDEN, new FloatFactoryStrategy(hiddenBias.createFactory(parallelism)))
                    .build();

            RandomSupportFloat randomSupport = parallelism.getRandomSupport(SettingsRandomType.UNIFORM);
            EnumFactory<SettingsActivationFunction> inputActivationFunctionFactory = genomeFactory.getInputActivationFunction().createFactory(parallelism);
            EnumFactory<SettingsOutputActivationFunction> outputActivationFunctionFactory = genomeFactory.getOutputActivationFunction().createFactory(parallelism);
            EnumFactory<SettingsActivationFunction> hiddenActivationFunctionFactory = hiddenActivationFunction.createFactory(parallelism);

            Map<NodeGeneType, ActivationFunctionFactory> activationFunctionFactories = ImmutableMap.<NodeGeneType, ActivationFunctionFactory>builder()
                    .put(NodeGeneType.INPUT, createActivationFunctionFactory(inputActivationFunctionFactory, randomSupport))
                    .put(NodeGeneType.OUTPUT, createActivationFunctionFactory(outputActivationFunctionFactory, hiddenActivationFunctionFactory, randomSupport))
                    .put(NodeGeneType.BIAS, new ActivationFunctionFactoryLiteral(SettingsActivationFunction.IDENTITY))
                    .put(NodeGeneType.HIDDEN, createActivationFunctionFactory(hiddenActivationFunctionFactory, randomSupport))
                    .build();

            return new ContextDefaultNodeGeneSupport(sequentialIdFactories, biasFactories, activationFunctionFactories);
        };
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class FloatFactoryStrategy implements FloatFactory {
        private final FloatFactory floatFactory;

        @Override
        public float create() {
            return floatFactory.create();
        }
    }

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class FloatFactoryNoBias implements FloatFactory {
        @Override
        public float create() {
            throw new IllegalStateException("there are no biases allowed in this genome");
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class FloatFactoryBiasDefault implements FloatFactory {
        private final List<FloatFactory> biasNodeBiasFactories;
        private int index = 0;

        @Override
        public float create() {
            return biasNodeBiasFactories.get(index++).create();
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class FloatFactoryBiasConcurrent implements FloatFactory {
        private final List<FloatFactory> biasNodeBiasFactories;
        private final AtomicInteger index = new AtomicInteger();

        @Override
        public float create() {
            return biasNodeBiasFactories.get(index.getAndIncrement()).create();
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class ActivationFunctionFactoryLiteral implements ActivationFunctionFactory {
        private final SettingsActivationFunction activationFunction;

        @Override
        public ActivationFunction next() {
            return ACTIVATION_FUNCTIONS_MAP.get(activationFunction);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class ActivationFunctionFactoryUnknown implements ActivationFunctionFactory {
        private final SettingsActivationFunction activationFunction;
        private final RandomSupportFloat randomSupport;

        @Override
        public ActivationFunction next() {
            if (activationFunction == SettingsActivationFunction.RANDOM) {
                int index = randomSupport.next(0, ACTIVATION_FUNCTIONS.size());

                return ACTIVATION_FUNCTIONS.get(index);
            }

            return ACTIVATION_FUNCTIONS_MAP.get(activationFunction);
        }
    }
}
