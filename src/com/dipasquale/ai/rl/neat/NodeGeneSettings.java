package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.factory.ActivationFunctionTypeFactory;
import com.dipasquale.ai.rl.neat.factory.CyclicElementFloatFactory;
import com.dipasquale.ai.rl.neat.factory.NoopRecurrentWeightFactory;
import com.dipasquale.ai.rl.neat.factory.OutputActivationFunctionFactory;
import com.dipasquale.ai.rl.neat.factory.RecurrentWeightFactory;
import com.dipasquale.ai.rl.neat.factory.StrategyActivationFunctionFactory;
import com.dipasquale.ai.rl.neat.factory.StrategyRecurrentWeightFactory;
import com.dipasquale.ai.rl.neat.function.activation.ActivationFunctionType;
import com.dipasquale.ai.rl.neat.function.activation.OutputActivationFunctionType;
import com.dipasquale.ai.rl.neat.genotype.HistoricalMarkings;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneIdFactory;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.factory.ConstantFloatFactory;
import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.factory.IllegalStateFloatFactory;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class NodeGeneSettings {
    @Builder.Default
    private final FloatNumber inputBias = FloatNumber.constant(0f);
    @Builder.Default
    private final EnumValue<ActivationFunctionType> inputActivationFunction = EnumValue.constant(ActivationFunctionType.IDENTITY);
    @Builder.Default
    private final FloatNumber outputBias = FloatNumber.random(RandomType.UNIFORM, 2f);
    @Builder.Default
    private final EnumValue<OutputActivationFunctionType> outputActivationFunction = EnumValue.constant(OutputActivationFunctionType.SIGMOID);
    @Builder.Default
    private final FloatNumber hiddenBias = FloatNumber.random(RandomType.UNIFORM, 4f);
    @Builder.Default
    private final EnumValue<ActivationFunctionType> hiddenActivationFunction = EnumValue.constant(ActivationFunctionType.SIGMOID);

    private static FloatFactory createBiasFactory(final List<Float> biases) {
        if (biases.isEmpty()) {
            return new IllegalStateFloatFactory("there are no biases allowed in this genome");
        }

        List<FloatFactory> biasNodeBiasFactories = biases.stream()
                .filter(Float::isFinite)
                .map(ConstantFloatFactory::new)
                .collect(Collectors.toList());

        return new CyclicElementFloatFactory(biasNodeBiasFactories);
    }

    private static Map<NodeGeneType, FloatFactory> createBiasFactories(final NeatInitializationContext initializationContext, final GenesisGenomeTemplate genesisGenomeTemplate, final NodeGeneSettings nodeGeneSettings) {
        Map<NodeGeneType, FloatFactory> biasFactories = new EnumMap<>(NodeGeneType.class);

        biasFactories.put(NodeGeneType.INPUT, nodeGeneSettings.inputBias.createFactory(initializationContext, "nodes.inputBias"));
        biasFactories.put(NodeGeneType.OUTPUT, nodeGeneSettings.outputBias.createFactory(initializationContext, "nodes.outputBias"));
        biasFactories.put(NodeGeneType.BIAS, createBiasFactory(genesisGenomeTemplate.getBiases()));
        biasFactories.put(NodeGeneType.HIDDEN, nodeGeneSettings.hiddenBias.createFactory(initializationContext, "nodes.hiddenBias"));

        return biasFactories;
    }

    private static RecurrentWeightFactory createRecurrentBiasesFactory(final ConnectionGeneSettings connectionGeneSettings, final FloatFactory biasFactory) {
        float recurrentAllowanceRate = connectionGeneSettings.getRecurrentAllowanceRate();

        if (Float.compare(recurrentAllowanceRate, 0f) <= 0) {
            return NoopRecurrentWeightFactory.getInstance();
        }

        return new StrategyRecurrentWeightFactory(biasFactory, connectionGeneSettings.getRecurrentStateType());
    }

    private static Map<NodeGeneType, RecurrentWeightFactory> createRecurrentBiasesFactories(final Map<NodeGeneType, FloatFactory> biasFactories, final ConnectionGeneSettings connectionGeneSettings) {
        Map<NodeGeneType, RecurrentWeightFactory> recurrentBiasesFactories = new EnumMap<>(NodeGeneType.class);

        for (NodeGeneType type : biasFactories.keySet()) {
            FloatFactory biasFactory = biasFactories.get(type);
            RecurrentWeightFactory recurrentBiasFactory = createRecurrentBiasesFactory(connectionGeneSettings, biasFactory);

            recurrentBiasesFactories.put(type, recurrentBiasFactory);
        }

        return recurrentBiasesFactories;
    }

    private static StrategyActivationFunctionFactory createActivationFunctionFactory(final NeatInitializationContext initializationContext, final EnumValue<ActivationFunctionType> activationFunctionType) {
        EnumFactory<ActivationFunctionType> enumFactory = activationFunctionType.createFactory(initializationContext);
        ActivationFunctionTypeFactory activationFunctionFactory = new ActivationFunctionTypeFactory(enumFactory);

        return new StrategyActivationFunctionFactory(activationFunctionFactory);
    }

    private static StrategyActivationFunctionFactory createActivationFunctionFactory(final NeatInitializationContext initializationContext, final EnumValue<OutputActivationFunctionType> outputActivationFunctionType, final EnumValue<ActivationFunctionType> hiddenActivationFunctionType) {
        EnumFactory<OutputActivationFunctionType> outputEnumFactory = outputActivationFunctionType.createFactory(initializationContext);
        EnumFactory<ActivationFunctionType> hiddenEnumFactory = hiddenActivationFunctionType.createFactory(initializationContext);
        OutputActivationFunctionFactory activationFunctionFactory = new OutputActivationFunctionFactory(outputEnumFactory, hiddenEnumFactory);

        return new StrategyActivationFunctionFactory(activationFunctionFactory);
    }

    private static Map<NodeGeneType, StrategyActivationFunctionFactory> createActivationFunctionFactories(final NeatInitializationContext initializationContext, final NodeGeneSettings nodeGeneSettings) {
        Map<NodeGeneType, StrategyActivationFunctionFactory> activationFunctionFactories = new EnumMap<>(NodeGeneType.class);

        activationFunctionFactories.put(NodeGeneType.INPUT, createActivationFunctionFactory(initializationContext, nodeGeneSettings.inputActivationFunction));
        activationFunctionFactories.put(NodeGeneType.OUTPUT, createActivationFunctionFactory(initializationContext, nodeGeneSettings.outputActivationFunction, nodeGeneSettings.hiddenActivationFunction));
        activationFunctionFactories.put(NodeGeneType.BIAS, createActivationFunctionFactory(initializationContext, EnumValue.constant(ActivationFunctionType.IDENTITY)));
        activationFunctionFactories.put(NodeGeneType.HIDDEN, createActivationFunctionFactory(initializationContext, nodeGeneSettings.hiddenActivationFunction));

        return activationFunctionFactories;
    }

    private static int getInputCount(final GenesisGenomeTemplate genesisGenomeTemplate) {
        int inputCount = genesisGenomeTemplate.getInputs();

        ArgumentValidatorSupport.ensureGreaterThanZero(inputCount, "genesisGenomeTemplate.inputs");

        return inputCount;
    }

    private static int getOutputCount(final GenesisGenomeTemplate genesisGenomeTemplate) {
        int outputCount = genesisGenomeTemplate.getOutputs();

        ArgumentValidatorSupport.ensureGreaterThanZero(outputCount, "genesisGenomeTemplate.outputs");

        return outputCount;
    }

    private static int getBiasCount(final GenesisGenomeTemplate genesisGenomeTemplate) {
        boolean isValid = genesisGenomeTemplate.getBiases().stream()
                .allMatch(bias -> bias != null && !Float.isInfinite(bias));

        if (!isValid) {
            String message = "genesisGenomeTemplate.biases cannot be null or infinite";

            throw new IllegalArgumentException(message);
        }

        return genesisGenomeTemplate.getBiases().size();
    }

    private static int getHiddenCount(final GenesisGenomeTemplate genesisGenomeTemplate) {
        int hiddenCount = 0;

        for (Integer hiddenLayer : genesisGenomeTemplate.getHiddenLayers()) {
            if (hiddenLayer == null || hiddenLayer < 1) {
                String message = "genesisGenomeTemplate.hiddenLayers cannot be null or less than 1";

                throw new IllegalArgumentException(message);
            }

            hiddenCount += hiddenLayer;
        }

        return hiddenCount;
    }

    DefaultNeatContextNodeGeneSupport create(final NeatInitializationContext initializationContext, final GenesisGenomeTemplate genesisGenomeTemplate, final ConnectionGeneSettings connectionGeneSettings) {
        NodeGeneIdFactory nodeGeneIdFactory = new NodeGeneIdFactory();
        Map<NodeGeneType, FloatFactory> biasFactories = createBiasFactories(initializationContext, genesisGenomeTemplate, this);
        Map<NodeGeneType, RecurrentWeightFactory> recurrentBiasesFactories = createRecurrentBiasesFactories(biasFactories, connectionGeneSettings);
        Map<NodeGeneType, StrategyActivationFunctionFactory> activationFunctionFactories = createActivationFunctionFactories(initializationContext, this);

        DefaultNeatContextNodeGeneSupport.NodeGeneTemplateParams nodeGeneTemplateParams = DefaultNeatContextNodeGeneSupport.NodeGeneTemplateParams.builder() // TODO: revise this, validation is inconsistent
                .inputCount(getInputCount(genesisGenomeTemplate))
                .outputCount(getOutputCount(genesisGenomeTemplate))
                .biasCount(getBiasCount(genesisGenomeTemplate))
                .hiddenCount(getHiddenCount(genesisGenomeTemplate))
                .build();

        HistoricalMarkings historicalMarkings = initializationContext.getHistoricalMarkings();

        return new DefaultNeatContextNodeGeneSupport(nodeGeneIdFactory, biasFactories, recurrentBiasesFactories, activationFunctionFactories, nodeGeneTemplateParams, historicalMarkings);
    }
}
