package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.common.SequentialIdFactoryLong;
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
            .put(SettingsActivationFunction.Identity, ActivationFunction.Identity)
            .put(SettingsActivationFunction.ReLU, ActivationFunction.ReLU)
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
    private final SettingsActivationFunction hiddenActivationFunction = SettingsActivationFunction.ReLU;

    private static ActivationFunction getActivationFunction(final SettingsActivationFunction activationFunction) {
        if (activationFunction == SettingsActivationFunction.Random) {
            int index = RANDOM_SUPPORT.next(0, ACTIVATION_FUNCTIONS.size());

            return ACTIVATION_FUNCTIONS.get(index);
        }

        return ACTIVATION_FUNCTIONS_MAP.get(activationFunction);
    }

    ContextDefaultComponentFactory<ContextDefaultNodeGeneSupport> createFactory(final SettingsGenomeFactory genomeFactory) {
        Map<NodeGeneType, SequentialIdFactory> sequentialIdFactories = ImmutableMap.<NodeGeneType, SequentialIdFactory>builder()
                .put(NodeGeneType.Input, new SequentialIdFactoryDefault("n1-input", inputIdFactory))
                .put(NodeGeneType.Output, new SequentialIdFactoryDefault("n4-output", outputIdFactory))
                .put(NodeGeneType.Bias, new SequentialIdFactoryDefault("n2-output", biasIdFactory))
                .put(NodeGeneType.Hidden, new SequentialIdFactoryDefault("n3-output", hiddenIdFactory))
                .build();

        AtomicLoopSelector<SettingsFloatNumber> biasNodeBiasFactory = new AtomicLoopSelector<>(genomeFactory.getBiases()::get, 0, genomeFactory.getBiases().size(), true);

        Map<NodeGeneType, FloatFactory> biasFactories = ImmutableMap.<NodeGeneType, FloatFactory>builder()
                .put(NodeGeneType.Input, genomeFactory.getInputBias()::get)
                .put(NodeGeneType.Output, genomeFactory.getOutputBias()::get)
                .put(NodeGeneType.Bias, () -> biasNodeBiasFactory.next().get())
                .put(NodeGeneType.Hidden, hiddenBias::get)
                .build();

        Map<NodeGeneType, ActivationFunctionFactory> activationFunctionFactories = ImmutableMap.<NodeGeneType, ActivationFunctionFactory>builder()
                .put(NodeGeneType.Input, () -> getActivationFunction(genomeFactory.getInputActivationFunction()))
                .put(NodeGeneType.Output, () -> getActivationFunction(genomeFactory.getOutputActivationFunction()))
                .put(NodeGeneType.Bias, () -> ActivationFunction.Identity)
                .put(NodeGeneType.Hidden, () -> getActivationFunction(hiddenActivationFunction))
                .build();

        return c -> new ContextDefaultNodeGeneSupport(sequentialIdFactories, biasFactories, activationFunctionFactories);
    }
}
