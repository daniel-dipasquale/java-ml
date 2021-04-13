package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.ActivationFunctionIdentity;
import com.dipasquale.ai.common.ActivationFunctionProvider;
import com.dipasquale.ai.common.ActivationFunctionReLU;
import com.dipasquale.ai.common.ActivationFunctionSigmoid;
import com.dipasquale.ai.common.ActivationFunctionStep;
import com.dipasquale.ai.common.ActivationFunctionTanH;
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
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SettingsNodeGeneSupport {
    private static final Map<SettingsActivationFunction, ActivationFunction> ACTIVATION_FUNCTIONS_MAP = ImmutableMap.<SettingsActivationFunction, ActivationFunction>builder()
            .put(SettingsActivationFunction.IDENTITY, ActivationFunctionIdentity.getInstance())
            .put(SettingsActivationFunction.RE_LU, ActivationFunctionReLU.getInstance())
            .put(SettingsActivationFunction.SIGMOID, ActivationFunctionSigmoid.getInstance())
            .put(SettingsActivationFunction.TAN_H, ActivationFunctionTanH.getInstance())
            .put(SettingsActivationFunction.STEP, ActivationFunctionStep.getInstance())
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

    private static FloatFactory createBiasFactory(final List<SettingsFloatNumber> biases, final SettingsParallelismSupport parallelism) {
        if (biases.size() == 0) {
            return FloatFactory.createIllegalState("there are no biases allowed in this genome");
        }

        List<FloatFactory> biasNodeBiasFactories = biases.stream()
                .map(sfn -> sfn.createFactory(parallelism))
                .collect(Collectors.toList());

        return FloatFactory.createCyclic(biasNodeBiasFactories).selectContended(parallelism.isEnabled());
    }

    ContextDefaultNodeGeneSupport create(final SettingsGenesisGenomeTemplate genesisGenomeTemplate, final SettingsParallelismSupport parallelism) {
        Map<NodeGeneType, FloatFactory> biasFactories = ImmutableMap.<NodeGeneType, FloatFactory>builder()
                .put(NodeGeneType.INPUT, genesisGenomeTemplate.getInputBias().createFactory(parallelism))
                .put(NodeGeneType.OUTPUT, genesisGenomeTemplate.getOutputBias().createFactory(parallelism))
                .put(NodeGeneType.BIAS, createBiasFactory(genesisGenomeTemplate.getBiases(), parallelism))
                .put(NodeGeneType.HIDDEN, hiddenBias.createFactory(parallelism))
                .build();

        RandomSupportFloat randomSupport = parallelism.getRandomSupport(SettingsRandomType.UNIFORM);
        EnumFactory<SettingsActivationFunction> inputActivationFunctionFactory = genesisGenomeTemplate.getInputActivationFunction().createFactory(parallelism);
        EnumFactory<SettingsOutputActivationFunction> outputActivationFunctionFactory = genesisGenomeTemplate.getOutputActivationFunction().createFactory(parallelism);
        EnumFactory<SettingsActivationFunction> hiddenActivationFunctionFactory = hiddenActivationFunction.createFactory(parallelism);

        Map<NodeGeneType, ActivationFunctionProvider> activationFunctionFactories = ImmutableMap.<NodeGeneType, ActivationFunctionProvider>builder()
                .put(NodeGeneType.INPUT, new ActivationFunctionProviderDefault(inputActivationFunctionFactory, randomSupport))
                .put(NodeGeneType.OUTPUT, new ActivationFunctionProviderOutput(outputActivationFunctionFactory, hiddenActivationFunctionFactory, randomSupport))
                .put(NodeGeneType.BIAS, new ActivationFunctionProviderLiteral(SettingsActivationFunction.IDENTITY))
                .put(NodeGeneType.HIDDEN, new ActivationFunctionProviderDefault(hiddenActivationFunctionFactory, randomSupport))
                .build();

        int inputs = genesisGenomeTemplate.getInputs().createFactory(parallelism).create();
        int outputs = genesisGenomeTemplate.getOutputs().createFactory(parallelism).create();
        int biases = genesisGenomeTemplate.getBiases().size();

        return new ContextDefaultNodeGeneSupport(biasFactories, activationFunctionFactories, inputs, outputs, biases);
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class ActivationFunctionProviderDefault implements ActivationFunctionProvider {
        @Serial
        private static final long serialVersionUID = 6954949568299530193L;
        private final EnumFactory<SettingsActivationFunction> activationFunctionFactory;
        private final RandomSupportFloat randomSupport;

        @Override
        public ActivationFunction get() {
            SettingsActivationFunction activationFunction = activationFunctionFactory.create();

            return getActivationFunction(activationFunction, randomSupport);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class ActivationFunctionProviderOutput implements ActivationFunctionProvider {
        @Serial
        private static final long serialVersionUID = -428770097116120238L;
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

    private static final class ActivationFunctionProviderLiteral implements ActivationFunctionProvider {
        @Serial
        private static final long serialVersionUID = -4677123004217102288L;
        private final ActivationFunction activationFunction;

        ActivationFunctionProviderLiteral(final SettingsActivationFunction activationFunction) {
            this.activationFunction = ACTIVATION_FUNCTIONS_MAP.get(activationFunction);
        }

        @Override
        public ActivationFunction get() {
            return activationFunction;
        }
    }
}
