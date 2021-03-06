package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.SequentialIdFactory;
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
public final class SettingsNodeGeneSupport<T extends Comparable<T>> {
    private static final RandomSupportFloat RANDOM_SUPPORT = RandomSupportFloat.createConcurrent();

    private static final Map<SettingsActivationFunction, ActivationFunction> ACTIVATION_FUNCTIONS_MAP = ImmutableMap.<SettingsActivationFunction, ActivationFunction>builder()
            .put(SettingsActivationFunction.Identity, ActivationFunction.Identity)
            .put(SettingsActivationFunction.ReLU, ActivationFunction.ReLU)
            .build();

    private static final List<ActivationFunction> ACTIVATION_FUNCTIONS = ImmutableList.copyOf(ACTIVATION_FUNCTIONS_MAP.values());
    private final SequentialIdFactory<T> inputIdFactory;
    private final SequentialIdFactory<T> outputIdFactory;
    private final SequentialIdFactory<T> biasIdFactory;
    private final SequentialIdFactory<T> hiddenIdFactory;
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

    ContextDefaultComponentFactory<T, ContextDefaultNodeGeneSupport<T>> createFactory(final SettingsGenomeFactory genomeFactory) {
        Map<NodeGeneType, SequentialIdFactory<T>> sequentialIdFactories = ImmutableMap.<NodeGeneType, SequentialIdFactory<T>>builder()
                .put(NodeGeneType.Input, inputIdFactory)
                .put(NodeGeneType.Output, outputIdFactory)
                .put(NodeGeneType.Bias, biasIdFactory)
                .put(NodeGeneType.Hidden, hiddenIdFactory)
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

        return c -> new ContextDefaultNodeGeneSupport<>(sequentialIdFactories, biasFactories, activationFunctionFactories);
    }
}
