package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.ActivationFunctionFactory;
import com.dipasquale.ai.common.FloatFactory;
import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.common.SequentialIdFactoryLong;
import com.dipasquale.ai.rl.neat.context.ContextDefaultComponentFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultNodeGeneSupport;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.concurrent.AtomicLoopSelector;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.Map;

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
    private final SequentialIdFactory inputIdFactory = new SequentialIdFactoryLong();
    @Builder.Default
    private final SequentialIdFactory outputIdFactory = new SequentialIdFactoryLong();
    @Builder.Default
    private final SequentialIdFactory biasIdFactory = new SequentialIdFactoryLong();
    @Builder.Default
    private final SequentialIdFactory hiddenIdFactory = new SequentialIdFactoryLong();
    @Builder.Default
    private final SettingsFloatNumber hiddenBias = SettingsFloatNumber.literal(0f);
    @Builder.Default
    private final SettingsEnum<SettingsActivationFunction> hiddenActivationFunction = SettingsEnum.literal(SettingsActivationFunction.RE_LU);

    private static ActivationFunction getActivationFunction(final SettingsActivationFunction activationFunction) {
        if (activationFunction == SettingsActivationFunction.RANDOM) {
            int index = SettingsConstants.RANDOM_SUPPORT_UNIFORM.next(0, ACTIVATION_FUNCTIONS.size());

            return ACTIVATION_FUNCTIONS.get(index);
        }

        return ACTIVATION_FUNCTIONS_MAP.get(activationFunction);
    }

    private static ActivationFunction getActivationFunction(final SettingsEnum<SettingsActivationFunction> activationFunction) {
        SettingsActivationFunction activationFunctionFixed = activationFunction.get();

        if (activationFunctionFixed == SettingsActivationFunction.RANDOM) {
            int index = SettingsConstants.RANDOM_SUPPORT_UNIFORM.next(0, ACTIVATION_FUNCTIONS.size());

            return ACTIVATION_FUNCTIONS.get(index);
        }

        return ACTIVATION_FUNCTIONS_MAP.get(activationFunctionFixed);
    }

    private static ActivationFunction getActivationFunction(final SettingsEnum<SettingsOutputActivationFunction> outputActivationFunction, final SettingsEnum<SettingsActivationFunction> activationFunction) {
        SettingsOutputActivationFunction outputActivationFunctionFixed = outputActivationFunction.get();

        return switch (outputActivationFunctionFixed) {
            case RANDOM -> getActivationFunction(SettingsActivationFunction.RANDOM);

            case IDENTITY -> getActivationFunction(SettingsActivationFunction.IDENTITY);

            case RE_LU -> getActivationFunction(SettingsActivationFunction.RE_LU);

            case SIGMOID -> getActivationFunction(SettingsActivationFunction.SIGMOID);

            default -> getActivationFunction(activationFunction);
        };
    }

    private static FloatFactory createBiasFactoryForBiasNode(final SettingsGenomeFactory genomeFactory) {
        if (genomeFactory.getBiases().size() == 0) {
            return () -> {
                throw new IllegalStateException("there are no biases allowed in this genome");
            };
        }

        AtomicLoopSelector<SettingsFloatNumber> biasNodeBiasFactory = new AtomicLoopSelector<>(genomeFactory.getBiases()::get, 0, genomeFactory.getBiases().size(), true);

        return () -> biasNodeBiasFactory.next().get();
    }

    ContextDefaultComponentFactory<ContextDefaultNodeGeneSupport> createFactory(final SettingsGenomeFactory genomeFactory) {
        Map<NodeGeneType, SequentialIdFactory> sequentialIdFactories = ImmutableMap.<NodeGeneType, SequentialIdFactory>builder()
                .put(NodeGeneType.INPUT, new SequentialIdFactoryDefault("n1_input", inputIdFactory))
                .put(NodeGeneType.OUTPUT, new SequentialIdFactoryDefault("n4_output", outputIdFactory))
                .put(NodeGeneType.BIAS, new SequentialIdFactoryDefault("n2_bias", biasIdFactory))
                .put(NodeGeneType.HIDDEN, new SequentialIdFactoryDefault("n3_hidden", hiddenIdFactory))
                .build();

        Map<NodeGeneType, FloatFactory> biasFactories = ImmutableMap.<NodeGeneType, FloatFactory>builder()
                .put(NodeGeneType.INPUT, genomeFactory.getInputBias()::get)
                .put(NodeGeneType.OUTPUT, genomeFactory.getOutputBias()::get)
                .put(NodeGeneType.BIAS, createBiasFactoryForBiasNode(genomeFactory))
                .put(NodeGeneType.HIDDEN, hiddenBias::get)
                .build();

        Map<NodeGeneType, ActivationFunctionFactory> activationFunctionFactories = ImmutableMap.<NodeGeneType, ActivationFunctionFactory>builder()
                .put(NodeGeneType.INPUT, () -> getActivationFunction(genomeFactory.getInputActivationFunction()))
                .put(NodeGeneType.OUTPUT, () -> getActivationFunction(genomeFactory.getOutputActivationFunction(), hiddenActivationFunction))
                .put(NodeGeneType.BIAS, () -> ActivationFunction.IDENTITY)
                .put(NodeGeneType.HIDDEN, () -> getActivationFunction(hiddenActivationFunction))
                .build();

        return context -> new ContextDefaultNodeGeneSupport(sequentialIdFactories, biasFactories, activationFunctionFactories);
    }
}
