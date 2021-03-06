package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.concurrent.AtomicLoopSelector;
import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Map;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsNodeGeneSupport<T extends Comparable<T>> {
    private static final Map<SettingsInitialActivationFunction, ActivationFunction> ACTIVATION_FUNCTIONS_MAP = ImmutableMap.<SettingsInitialActivationFunction, ActivationFunction>builder()
            .put(SettingsInitialActivationFunction.Identity, ActivationFunction.Identity)
            .put(SettingsInitialActivationFunction.ReLU, ActivationFunction.ReLU)
            .build();

    private final SequentialIdFactory<T> inputIdFactory;
    private final SequentialIdFactory<T> outputIdFactory;
    private final SequentialIdFactory<T> biasIdFactory;
    private final SequentialIdFactory<T> hiddenIdFactory;
    private final SettingsFloatNumber hiddenBias;

    private ActivationFunction getActivationFunction(final SettingsInitialActivationFunction initialActivationFunction) {
        return ACTIVATION_FUNCTIONS_MAP.get(initialActivationFunction);
    }

    ContextDefaultComponentFactory<T, ContextDefaultNodeGeneSupport<T>> createFactory(final SettingsGenomeFactory genomeFactory) {
        Map<NodeGeneType, SequentialIdFactory<T>> sequentialIdFactories = ImmutableMap.<NodeGeneType, SequentialIdFactory<T>>builder()
                .put(NodeGeneType.Input, inputIdFactory)
                .put(NodeGeneType.Output, outputIdFactory)
                .put(NodeGeneType.Bias, biasIdFactory)
                .put(NodeGeneType.Hidden, hiddenIdFactory)
                .build();

        AtomicLoopSelector<SettingsFloatNumber> biasNodeBiasFactory = new AtomicLoopSelector<>(genomeFactory.getBiases()::get, 0, genomeFactory.getBiases().size(), true);

        Map<NodeGeneType, NodeGeneBiasFactory> biasFactories = ImmutableMap.<NodeGeneType, NodeGeneBiasFactory>builder()
                .put(NodeGeneType.Input, genomeFactory.getInputBias()::get)
                .put(NodeGeneType.Output, genomeFactory.getOutputBias()::get)
                .put(NodeGeneType.Bias, () -> biasNodeBiasFactory.next().get())
                .put(NodeGeneType.Hidden, hiddenBias::get)
                .build();

        Map<NodeGeneType, ActivationFunctionFactory> activationFunctionFactories = ImmutableMap.<NodeGeneType, ActivationFunctionFactory>builder()
                .put(NodeGeneType.Input, () -> getActivationFunction(genomeFactory.getInputActivationFunction()))
                .put(NodeGeneType.Output, () -> getActivationFunction(genomeFactory.getOutputActivationFunction()))
                .put(NodeGeneType.Bias, () -> ActivationFunction.Identity)
                .put(NodeGeneType.Hidden, null) // TODO: finish
                .build();

        return c -> new ContextDefaultNodeGeneSupport<>(sequentialIdFactories, biasFactories, null);
    }
}
