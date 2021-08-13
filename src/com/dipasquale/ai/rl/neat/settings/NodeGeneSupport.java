/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.common.function.activation.ActivationFunctionFactory;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.common.function.activation.OutputActivationFunctionType;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.context.DefaultNodeGeneSupportContext;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.ai.rl.neat.switcher.factory.DefaultActivationFunctionFactorySwitcher;
import com.dipasquale.ai.rl.neat.switcher.factory.LiteralActivationFunctionFactorySwitcher;
import com.dipasquale.ai.rl.neat.switcher.factory.OutputActivationFunctionFactorySwitcher;
import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.factory.IllegalStateFloatFactory;
import com.dipasquale.common.switcher.DefaultObjectSwitcher;
import com.dipasquale.common.switcher.ObjectSwitcher;
import com.dipasquale.common.switcher.factory.CyclicFloatFactorySwitcher;
import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class NodeGeneSupport {
    @Builder.Default
    private final FloatNumber inputBias = FloatNumber.literal(0f);
    @Builder.Default
    private final EnumValue<ActivationFunctionType> inputActivationFunction = EnumValue.literal(ActivationFunctionType.IDENTITY);
    @Builder.Default
    private final FloatNumber outputBias = FloatNumber.random(RandomType.UNIFORM, -1f, 1f);
    @Builder.Default
    private final EnumValue<OutputActivationFunctionType> outputActivationFunction = EnumValue.literal(OutputActivationFunctionType.SIGMOID);
    @Builder.Default
    private final FloatNumber hiddenBias = FloatNumber.random(RandomType.UNIFORM, -1f, 1f);
    @Builder.Default
    private final EnumValue<ActivationFunctionType> hiddenActivationFunction = EnumValue.literal(ActivationFunctionType.TAN_H);

    private static ObjectSwitcher<FloatFactory> createBiasFactorySwitcher(final List<FloatNumber> biases, final ParallelismSupport parallelism) {
        if (biases.isEmpty()) {
            IllegalStateFloatFactory floatFactory = new IllegalStateFloatFactory("there are no biases allowed in this genome");

            return new DefaultObjectSwitcher<>(parallelism.isEnabled(), floatFactory);
        }

        Iterable<Pair<FloatFactory>> biasNodeBiasFactoryPairs = biases.stream()
                .map(sfn -> ObjectSwitcher.deconstruct(sfn.createFactorySwitcher(parallelism)))
                ::iterator;

        return new CyclicFloatFactorySwitcher(parallelism.isEnabled(), biasNodeBiasFactoryPairs);
    }

    private static ObjectSwitcher<ActivationFunctionFactory> createActivationFunctionFactorySwitcher(final ObjectSwitcher<EnumFactory<ActivationFunctionType>> activationFunctionTypeFactorySwitcher, final ParallelismSupport parallelism) {
        Pair<EnumFactory<ActivationFunctionType>> activationFunctionTypeFactoryPair = ObjectSwitcher.deconstruct(activationFunctionTypeFactorySwitcher);

        return new DefaultActivationFunctionFactorySwitcher(parallelism.isEnabled(), activationFunctionTypeFactoryPair);
    }

    private static ObjectSwitcher<ActivationFunctionFactory> createActivationFunctionFactorySwitcher(final ObjectSwitcher<EnumFactory<OutputActivationFunctionType>> outputActivationFunctionTypeFactorySwitcher, final ObjectSwitcher<EnumFactory<ActivationFunctionType>> hiddenActivationFunctionTypeFactorySwitcher, final ParallelismSupport parallelism) {
        Pair<EnumFactory<OutputActivationFunctionType>> outputActivationFunctionTypeFactoryPair = ObjectSwitcher.deconstruct(outputActivationFunctionTypeFactorySwitcher);
        Pair<EnumFactory<ActivationFunctionType>> hiddenActivationFunctionTypeFactoryPair = ObjectSwitcher.deconstruct(hiddenActivationFunctionTypeFactorySwitcher);

        return new OutputActivationFunctionFactorySwitcher(parallelism.isEnabled(), outputActivationFunctionTypeFactoryPair, hiddenActivationFunctionTypeFactoryPair);
    }

    private static ObjectSwitcher<ActivationFunctionFactory> createActivationFunctionFactorySwitcher(final ActivationFunctionType activationFunctionType, final ParallelismSupport parallelism) {
        return new LiteralActivationFunctionFactorySwitcher(parallelism.isEnabled(), activationFunctionType);
    }

    DefaultNodeGeneSupportContext create(final GenesisGenomeTemplate genesisGenomeTemplate, final ParallelismSupport parallelism) {
        Map<NodeGeneType, ObjectSwitcher<FloatFactory>> biasFactorySwitchers = ImmutableMap.<NodeGeneType, ObjectSwitcher<FloatFactory>>builder()
                .put(NodeGeneType.INPUT, inputBias.createFactorySwitcher(parallelism))
                .put(NodeGeneType.OUTPUT, outputBias.createFactorySwitcher(parallelism))
                .put(NodeGeneType.BIAS, createBiasFactorySwitcher(genesisGenomeTemplate.getBiases(), parallelism))
                .put(NodeGeneType.HIDDEN, hiddenBias.createFactorySwitcher(parallelism))
                .build();

        ObjectSwitcher<EnumFactory<ActivationFunctionType>> inputActivationFunctionTypeFactorySwitcher = inputActivationFunction.createFactorySwitcher(parallelism);
        ObjectSwitcher<EnumFactory<OutputActivationFunctionType>> outputActivationFunctionTypeFactorySwitcher = outputActivationFunction.createFactorySwitcher(parallelism);
        ObjectSwitcher<EnumFactory<ActivationFunctionType>> hiddenActivationFunctionTypeFactorySwitcher = hiddenActivationFunction.createFactorySwitcher(parallelism);

        Map<NodeGeneType, ObjectSwitcher<ActivationFunctionFactory>> activationFunctionFactorySwitchers = ImmutableMap.<NodeGeneType, ObjectSwitcher<ActivationFunctionFactory>>builder()
                .put(NodeGeneType.INPUT, createActivationFunctionFactorySwitcher(inputActivationFunctionTypeFactorySwitcher, parallelism))
                .put(NodeGeneType.OUTPUT, createActivationFunctionFactorySwitcher(outputActivationFunctionTypeFactorySwitcher, hiddenActivationFunctionTypeFactorySwitcher, parallelism))
                .put(NodeGeneType.BIAS, createActivationFunctionFactorySwitcher(ActivationFunctionType.IDENTITY, parallelism))
                .put(NodeGeneType.HIDDEN, createActivationFunctionFactorySwitcher(hiddenActivationFunctionTypeFactorySwitcher, parallelism))
                .build();

        int inputs = genesisGenomeTemplate.getInputs().createFactorySwitcher(parallelism).getObject().create();
        int outputs = genesisGenomeTemplate.getOutputs().createFactorySwitcher(parallelism).getObject().create();
        int biases = genesisGenomeTemplate.getBiases().size();

        return new DefaultNodeGeneSupportContext(biasFactorySwitchers, activationFunctionFactorySwitchers, inputs, outputs, biases);
    }
}
