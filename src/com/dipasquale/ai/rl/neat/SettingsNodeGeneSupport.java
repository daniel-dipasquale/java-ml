package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.ActivationFunctionFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultNodeGeneSupport;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.common.EnumFactory;
import com.dipasquale.common.FloatFactory;
import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SettingsNodeGeneSupport {
    @Builder.Default
    private final SettingsFloatNumber hiddenBias = SettingsFloatNumber.random(SettingsRandomType.UNIFORM, -1f, 1f);
    @Builder.Default
    private final SettingsEnum<SettingsActivationFunction> hiddenActivationFunction = SettingsEnum.literal(SettingsActivationFunction.TAN_H);

    private static FloatFactory createBiasFactory(final List<SettingsFloatNumber> biases, final SettingsParallelismSupport parallelism) {
        if (biases.size() == 0) {
            return FloatFactory.createIllegalState("there are no biases allowed in this genome");
        }

        List<FloatFactory> biasNodeBiasFactories = biases.stream()
                .map(sfn -> sfn.createFactory(parallelism))
                .collect(Collectors.toList());

        FloatFactory factory = FloatFactory.createCyclic(biasNodeBiasFactories);

        return factory.selectContended(parallelism.isEnabled());
    }

    private static ActivationFunctionFactory createActivationFunctionFactory(final EnumFactory<SettingsActivationFunction> activationFunctionFactory, final SettingsParallelismSupport parallelism) {
        ActivationFunctionFactory factory = new SettingsActivationFunctionFactoryDefault(activationFunctionFactory);

        return factory.selectContended(parallelism.isEnabled());
    }

    private static ActivationFunctionFactory createActivationFunctionFactory(final EnumFactory<SettingsOutputActivationFunction> outputActivationFunctionFactory, final EnumFactory<SettingsActivationFunction> hiddenActivationFunctionFactory, final SettingsParallelismSupport parallelism) {
        ActivationFunctionFactory factory = new SettingsActivationFunctionFactoryOutput(outputActivationFunctionFactory, hiddenActivationFunctionFactory);

        return factory.selectContended(parallelism.isEnabled());
    }

    private static ActivationFunctionFactory createActivationFunctionFactory(final SettingsActivationFunction activationFunction, final SettingsParallelismSupport parallelism) {
        ActivationFunction activationFunctionFixed = SettingsConstants.getActivationFunction(activationFunction, parallelism.isEnabled());

        return ActivationFunctionFactory.createLiteral(activationFunctionFixed);
    }

    ContextDefaultNodeGeneSupport create(final SettingsGenesisGenomeTemplate genesisGenomeTemplate, final SettingsParallelismSupport parallelism) {
        Map<NodeGeneType, FloatFactory> biasFactories = ImmutableMap.<NodeGeneType, FloatFactory>builder()
                .put(NodeGeneType.INPUT, genesisGenomeTemplate.getInputBias().createFactory(parallelism))
                .put(NodeGeneType.OUTPUT, genesisGenomeTemplate.getOutputBias().createFactory(parallelism))
                .put(NodeGeneType.BIAS, createBiasFactory(genesisGenomeTemplate.getBiases(), parallelism))
                .put(NodeGeneType.HIDDEN, hiddenBias.createFactory(parallelism))
                .build();

        EnumFactory<SettingsActivationFunction> inputActivationFunctionFactory = genesisGenomeTemplate.getInputActivationFunction().createFactory(parallelism);
        EnumFactory<SettingsOutputActivationFunction> outputActivationFunctionFactory = genesisGenomeTemplate.getOutputActivationFunction().createFactory(parallelism);
        EnumFactory<SettingsActivationFunction> hiddenActivationFunctionFactory = hiddenActivationFunction.createFactory(parallelism);

        Map<NodeGeneType, ActivationFunctionFactory> activationFunctionFactories = ImmutableMap.<NodeGeneType, ActivationFunctionFactory>builder()
                .put(NodeGeneType.INPUT, createActivationFunctionFactory(inputActivationFunctionFactory, parallelism))
                .put(NodeGeneType.OUTPUT, createActivationFunctionFactory(outputActivationFunctionFactory, hiddenActivationFunctionFactory, parallelism))
                .put(NodeGeneType.BIAS, createActivationFunctionFactory(SettingsActivationFunction.IDENTITY, parallelism))
                .put(NodeGeneType.HIDDEN, createActivationFunctionFactory(hiddenActivationFunctionFactory, parallelism))
                .build();

        int inputs = genesisGenomeTemplate.getInputs().createFactory(parallelism).create();
        int outputs = genesisGenomeTemplate.getOutputs().createFactory(parallelism).create();
        int biases = genesisGenomeTemplate.getBiases().size();

        return new ContextDefaultNodeGeneSupport(biasFactories, activationFunctionFactories, inputs, outputs, biases);
    }
}
