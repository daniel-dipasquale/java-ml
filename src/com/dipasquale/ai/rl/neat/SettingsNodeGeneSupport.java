package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.ActivationFunctionFactory;
import com.dipasquale.ai.common.FloatFactory;
import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.common.SequentialIdFactoryLong;
import com.dipasquale.ai.rl.neat.context.ContextDefaultComponentFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultNodeGeneSupport;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.common.RandomSupportFloat;
import com.dipasquale.concurrent.AtomicLoopSelector;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsNodeGeneSupport {
    private static final RandomSupportFloat RANDOM_SUPPORT = RandomSupportFloat.createConcurrent();

    private static final Map<SettingsActivationFunction, ActivationFunction> ACTIVATION_FUNCTIONS_MAP = ImmutableMap.<SettingsActivationFunction, ActivationFunction>builder()
            .put(SettingsActivationFunction.Identity, ActivationFunction.IDENTITY)
            .put(SettingsActivationFunction.ReLU, ActivationFunction.RELU)
            .put(SettingsActivationFunction.Sigmoid, ActivationFunction.SIGMOID)
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
    private final SettingsEnum<SettingsActivationFunction> hiddenActivationFunction = SettingsEnum.literal(SettingsActivationFunction.ReLU);

    private static ActivationFunction getActivationFunction(final SettingsActivationFunction activationFunction) {
        if (activationFunction == SettingsActivationFunction.Random) {
            int index = RANDOM_SUPPORT.next(0, ACTIVATION_FUNCTIONS.size());

            return ACTIVATION_FUNCTIONS.get(index);
        }

        return ACTIVATION_FUNCTIONS_MAP.get(activationFunction);
    }

    private static ActivationFunction getActivationFunction(final SettingsEnum<SettingsActivationFunction> activationFunction) {
        SettingsActivationFunction activationFunctionFixed = activationFunction.get();

        if (activationFunctionFixed == SettingsActivationFunction.Random) {
            int index = RANDOM_SUPPORT.next(0, ACTIVATION_FUNCTIONS.size());

            return ACTIVATION_FUNCTIONS.get(index);
        }

        return ACTIVATION_FUNCTIONS_MAP.get(activationFunctionFixed);
    }

    private static ActivationFunction getActivationFunction(final SettingsEnum<SettingsOutputActivationFunction> outputActivationFunction, final SettingsEnum<SettingsActivationFunction> activationFunction) {
        SettingsOutputActivationFunction outputActivationFunctionFixed = outputActivationFunction.get();

        return switch (outputActivationFunctionFixed) {
            case Random -> getActivationFunction(SettingsActivationFunction.Random);

            case Identity -> getActivationFunction(SettingsActivationFunction.Identity);

            case ReLU -> getActivationFunction(SettingsActivationFunction.ReLU);

            case Sigmoid -> getActivationFunction(SettingsActivationFunction.Sigmoid);

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
                .put(NodeGeneType.Input, new SequentialIdFactoryDefault("n1_input", inputIdFactory))
                .put(NodeGeneType.Output, new SequentialIdFactoryDefault("n4_output", outputIdFactory))
                .put(NodeGeneType.Bias, new SequentialIdFactoryDefault("n2_bias", biasIdFactory))
                .put(NodeGeneType.Hidden, new SequentialIdFactoryDefault("n3_hidden", hiddenIdFactory))
                .build();

        Map<NodeGeneType, FloatFactory> biasFactories = ImmutableMap.<NodeGeneType, FloatFactory>builder()
                .put(NodeGeneType.Input, genomeFactory.getInputBias()::get)
                .put(NodeGeneType.Output, genomeFactory.getOutputBias()::get)
                .put(NodeGeneType.Bias, createBiasFactoryForBiasNode(genomeFactory))
                .put(NodeGeneType.Hidden, hiddenBias::get)
                .build();

        Map<NodeGeneType, ActivationFunctionFactory> activationFunctionFactories = ImmutableMap.<NodeGeneType, ActivationFunctionFactory>builder()
                .put(NodeGeneType.Input, () -> getActivationFunction(genomeFactory.getInputActivationFunction()))
                .put(NodeGeneType.Output, () -> getActivationFunction(genomeFactory.getOutputActivationFunction(), hiddenActivationFunction))
                .put(NodeGeneType.Bias, () -> ActivationFunction.IDENTITY)
                .put(NodeGeneType.Hidden, () -> getActivationFunction(hiddenActivationFunction))
                .build();

        return context -> new ContextDefaultNodeGeneSupport(sequentialIdFactories, biasFactories, activationFunctionFactories);
    }
}
