package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.ActivationFunctionIdentity;
import com.dipasquale.ai.common.ActivationFunctionProvider;
import com.dipasquale.ai.common.ActivationFunctionReLU;
import com.dipasquale.ai.common.ActivationFunctionSigmoid;
import com.dipasquale.ai.common.ActivationFunctionTanH;
import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.common.SequentialIdFactoryDefault;
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

import java.io.Serial;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SettingsNodeGeneSupport {
    private static final Map<SettingsActivationFunction, ActivationFunction> ACTIVATION_FUNCTIONS_MAP = ImmutableMap.<SettingsActivationFunction, ActivationFunction>builder()
            .put(SettingsActivationFunction.IDENTITY, ActivationFunctionIdentity.getInstance())
            .put(SettingsActivationFunction.RE_LU, ActivationFunctionReLU.getInstance())
            .put(SettingsActivationFunction.SIGMOID, ActivationFunctionSigmoid.getInstance())
            .put(SettingsActivationFunction.TAN_H, ActivationFunctionTanH.getInstance())
            .build();

    private static final List<ActivationFunction> ACTIVATION_FUNCTIONS = ImmutableList.copyOf(ACTIVATION_FUNCTIONS_MAP.values());
    @Builder.Default
    private final SettingsFloatNumber hiddenBias = SettingsFloatNumber.random(SettingsRandomType.UNIFORM, -1f, 1f);
    @Builder.Default
    private final SettingsEnum<SettingsActivationFunction> hiddenActivationFunction = SettingsEnum.literal(SettingsActivationFunction.TAN_H);

    public static ActivationFunction getActivationFunction(final SettingsActivationFunction activationFunction, final RandomSupportFloat randomSupport) {
        return switch (activationFunction) {
            case RANDOM -> {
                int index = randomSupport.next(0, ACTIVATION_FUNCTIONS.size());

                yield ACTIVATION_FUNCTIONS.get(index);
            }

            default -> ACTIVATION_FUNCTIONS_MAP.get(activationFunction);
        };
    }

    private static FloatFactory createBiasFactory(final List<SettingsFloatNumber> biases, final SettingsParallelism parallelism) {
        if (biases.size() == 0) {
            return new FloatFactoryNoBias();
        }

        List<FloatFactory> biasNodeBiasFactories = biases.stream()
                .map(sfn -> sfn.createFactory(parallelism))
                .collect(Collectors.toList());

        if (!parallelism.isEnabled()) {
            return new FloatFactoryBiasDefault(biasNodeBiasFactories);
        }

        return new FloatFactoryBiasConcurrent(biasNodeBiasFactories);
    }

    ContextDefaultNodeGeneSupport create(final SettingsGenomeFactory genomeFactory, final SettingsParallelism parallelism) {
        Map<NodeGeneType, SequentialIdFactory> sequentialIdFactories = ImmutableMap.<NodeGeneType, SequentialIdFactory>builder()
                .put(NodeGeneType.INPUT, parallelism.createSequentialIdFactory("n1_input", new SequentialIdFactoryDefault()))
                .put(NodeGeneType.OUTPUT, parallelism.createSequentialIdFactory("n4_output", new SequentialIdFactoryDefault()))
                .put(NodeGeneType.BIAS, parallelism.createSequentialIdFactory("n2_bias", new SequentialIdFactoryDefault()))
                .put(NodeGeneType.HIDDEN, parallelism.createSequentialIdFactory("n3_hidden", new SequentialIdFactoryDefault()))
                .build();

        Map<NodeGeneType, FloatFactory> biasFactories = ImmutableMap.<NodeGeneType, FloatFactory>builder()
                .put(NodeGeneType.INPUT, genomeFactory.getInputBias().createFactory(parallelism))
                .put(NodeGeneType.OUTPUT, genomeFactory.getOutputBias().createFactory(parallelism))
                .put(NodeGeneType.BIAS, createBiasFactory(genomeFactory.getBiases(), parallelism))
                .put(NodeGeneType.HIDDEN, hiddenBias.createFactory(parallelism))
                .build();

        RandomSupportFloat randomSupport = parallelism.getRandomSupport(SettingsRandomType.UNIFORM);
        EnumFactory<SettingsActivationFunction> inputActivationFunctionFactory = genomeFactory.getInputActivationFunction().createFactory(parallelism);
        EnumFactory<SettingsOutputActivationFunction> outputActivationFunctionFactory = genomeFactory.getOutputActivationFunction().createFactory(parallelism);
        EnumFactory<SettingsActivationFunction> hiddenActivationFunctionFactory = hiddenActivationFunction.createFactory(parallelism);

        Map<NodeGeneType, ActivationFunctionProvider> activationFunctionFactories = ImmutableMap.<NodeGeneType, ActivationFunctionProvider>builder()
                .put(NodeGeneType.INPUT, new ActivationFunctionFactoryDefault(inputActivationFunctionFactory, randomSupport))
                .put(NodeGeneType.OUTPUT, new ActivationFunctionFactoryOutput(outputActivationFunctionFactory, hiddenActivationFunctionFactory, randomSupport))
                .put(NodeGeneType.BIAS, new ActivationFunctionFactoryLiteral(SettingsActivationFunction.IDENTITY))
                .put(NodeGeneType.HIDDEN, new ActivationFunctionFactoryDefault(hiddenActivationFunctionFactory, randomSupport))
                .build();

        int inputs = genomeFactory.getInputs().createFactory(parallelism).create();
        int outputs = genomeFactory.getOutputs().createFactory(parallelism).create();
        int biases = genomeFactory.getBiases().size();

        return new ContextDefaultNodeGeneSupport(sequentialIdFactories, biasFactories, activationFunctionFactories, inputs, outputs, biases);
    }

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class FloatFactoryNoBias implements FloatFactory {
        @Serial
        private static final long serialVersionUID = 5535891217573360454L;

        @Override
        public float create() {
            throw new IllegalStateException("there are no biases allowed in this genome");
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class FloatFactoryBiasDefault implements FloatFactory {
        @Serial
        private static final long serialVersionUID = 342666697034548366L;
        private final List<FloatFactory> biasNodeBiasFactories;
        private int index = 0;

        @Override
        public float create() {
            int indexOld = index;

            index = (index + 1) % biasNodeBiasFactories.size();

            return biasNodeBiasFactories.get(indexOld).create();
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class FloatFactoryBiasConcurrent implements FloatFactory {
        @Serial
        private static final long serialVersionUID = 7213433767759259313L;
        private final List<FloatFactory> biasNodeBiasFactories;
        private final AtomicInteger index = new AtomicInteger();

        @Override
        public float create() {
            int indexFixed = index.getAndAccumulate(-1, (oi, ni) -> (oi + 1) % biasNodeBiasFactories.size());

            return biasNodeBiasFactories.get(indexFixed).create();
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class ActivationFunctionFactoryDefault implements ActivationFunctionProvider {
        @Serial
        private static final long serialVersionUID = 7277255622928403465L;
        private final EnumFactory<SettingsActivationFunction> activationFunctionFactory;
        private final RandomSupportFloat randomSupport;

        @Override
        public ActivationFunction get() {
            SettingsActivationFunction activationFunction = activationFunctionFactory.create();

            return getActivationFunction(activationFunction, randomSupport);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class ActivationFunctionFactoryOutput implements ActivationFunctionProvider {
        @Serial
        private static final long serialVersionUID = 4590482884547973964L;
        private final EnumFactory<SettingsOutputActivationFunction> outputActivationFunctionFactory;
        private final EnumFactory<SettingsActivationFunction> hiddenActivationFunctionFactory;
        private final RandomSupportFloat randomSupport;

        @Override
        public ActivationFunction get() {
            SettingsOutputActivationFunction outputActivationFunction = outputActivationFunctionFactory.create();

            return switch (outputActivationFunction) {
                case COPY_FROM_HIDDEN -> getActivationFunction(hiddenActivationFunctionFactory.create(), randomSupport);

                default -> getActivationFunction(outputActivationFunction.getTranslated(), randomSupport);
            };
        }
    }

    private static final class ActivationFunctionFactoryLiteral implements ActivationFunctionProvider {
        @Serial
        private static final long serialVersionUID = 1925932579626397814L;
        private final ActivationFunction activationFunction;

        ActivationFunctionFactoryLiteral(final SettingsActivationFunction activationFunction) {
            this.activationFunction = ACTIVATION_FUNCTIONS_MAP.get(activationFunction);
        }

        @Override
        public ActivationFunction get() {
            return activationFunction;
        }
    }
}
