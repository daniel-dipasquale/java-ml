package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.common.function.activation.ActivationFunctionFactory;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.common.function.activation.OutputActivationFunctionType;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.context.DefaultContextNodeGeneSupport;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype.DualModeNodeIdFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.profile.factory.DefaultActivationFunctionFactoryProfile;
import com.dipasquale.ai.rl.neat.synchronization.dual.profile.factory.LiteralActivationFunctionFactoryProfile;
import com.dipasquale.ai.rl.neat.synchronization.dual.profile.factory.OutputActivationFunctionFactoryProfile;
import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.factory.IllegalStateFloatFactory;
import com.dipasquale.synchronization.dual.profile.DefaultObjectProfile;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import com.dipasquale.synchronization.dual.profile.factory.CyclicFloatFactoryProfile;
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

    private static ObjectProfile<FloatFactory> createBiasFactoryProfile(final ParallelismSupport parallelism, final List<FloatNumber> biases) {
        if (biases.isEmpty()) {
            IllegalStateFloatFactory floatFactory = new IllegalStateFloatFactory("there are no biases allowed in this genome");

            return new DefaultObjectProfile<>(parallelism.isEnabled(), floatFactory);
        }

        Iterable<Pair<FloatFactory>> biasNodeBiasFactoryPairs = biases.stream()
                .map(sfn -> ObjectProfile.deconstruct(sfn.createFactoryProfile(parallelism)))
                ::iterator;

        return new CyclicFloatFactoryProfile(parallelism.isEnabled(), biasNodeBiasFactoryPairs);
    }

    private static ObjectProfile<ActivationFunctionFactory> createActivationFunctionFactoryProfile(final ParallelismSupport parallelism, final ObjectProfile<EnumFactory<ActivationFunctionType>> activationFunctionTypeFactoryProfile) {
        Pair<EnumFactory<ActivationFunctionType>> activationFunctionTypeFactoryPair = ObjectProfile.deconstruct(activationFunctionTypeFactoryProfile);

        return new DefaultActivationFunctionFactoryProfile(parallelism.isEnabled(), activationFunctionTypeFactoryPair);
    }

    private static ObjectProfile<ActivationFunctionFactory> createActivationFunctionFactoryProfile(final ParallelismSupport parallelism, final ObjectProfile<EnumFactory<OutputActivationFunctionType>> outputActivationFunctionTypeFactoryProfile, final ObjectProfile<EnumFactory<ActivationFunctionType>> hiddenActivationFunctionTypeFactoryProfile) {
        Pair<EnumFactory<OutputActivationFunctionType>> outputActivationFunctionTypeFactoryPair = ObjectProfile.deconstruct(outputActivationFunctionTypeFactoryProfile);
        Pair<EnumFactory<ActivationFunctionType>> hiddenActivationFunctionTypeFactoryPair = ObjectProfile.deconstruct(hiddenActivationFunctionTypeFactoryProfile);

        return new OutputActivationFunctionFactoryProfile(parallelism.isEnabled(), outputActivationFunctionTypeFactoryPair, hiddenActivationFunctionTypeFactoryPair);
    }

    DefaultContextNodeGeneSupport create(final GenesisGenomeTemplate genesisGenomeTemplate, final ParallelismSupport parallelism) {
        DualModeNodeIdFactory nodeIdFactory = new DualModeNodeIdFactory(parallelism.isEnabled());

        Map<NodeGeneType, ObjectProfile<FloatFactory>> biasFactoryProfiles = ImmutableMap.<NodeGeneType, ObjectProfile<FloatFactory>>builder()
                .put(NodeGeneType.INPUT, inputBias.createFactoryProfile(parallelism))
                .put(NodeGeneType.OUTPUT, outputBias.createFactoryProfile(parallelism))
                .put(NodeGeneType.BIAS, createBiasFactoryProfile(parallelism, genesisGenomeTemplate.getBiases()))
                .put(NodeGeneType.HIDDEN, hiddenBias.createFactoryProfile(parallelism))
                .build();

        ObjectProfile<EnumFactory<ActivationFunctionType>> inputActivationFunctionTypeFactoryProfile = inputActivationFunction.createFactoryProfile(parallelism);
        ObjectProfile<EnumFactory<OutputActivationFunctionType>> outputActivationFunctionTypeFactoryProfile = outputActivationFunction.createFactoryProfile(parallelism);
        ObjectProfile<EnumFactory<ActivationFunctionType>> hiddenActivationFunctionTypeFactoryProfile = hiddenActivationFunction.createFactoryProfile(parallelism);

        Map<NodeGeneType, ObjectProfile<ActivationFunctionFactory>> activationFunctionFactoryProfiles = ImmutableMap.<NodeGeneType, ObjectProfile<ActivationFunctionFactory>>builder()
                .put(NodeGeneType.INPUT, createActivationFunctionFactoryProfile(parallelism, inputActivationFunctionTypeFactoryProfile))
                .put(NodeGeneType.OUTPUT, createActivationFunctionFactoryProfile(parallelism, outputActivationFunctionTypeFactoryProfile, hiddenActivationFunctionTypeFactoryProfile))
                .put(NodeGeneType.BIAS, new LiteralActivationFunctionFactoryProfile(parallelism.isEnabled(), ActivationFunctionType.IDENTITY))
                .put(NodeGeneType.HIDDEN, createActivationFunctionFactoryProfile(parallelism, hiddenActivationFunctionTypeFactoryProfile))
                .build();

        int inputs = genesisGenomeTemplate.getInputs().createFactoryProfile(parallelism).getObject().create();
        int outputs = genesisGenomeTemplate.getOutputs().createFactoryProfile(parallelism).getObject().create();
        int biases = genesisGenomeTemplate.getBiases().size();

        return new DefaultContextNodeGeneSupport(nodeIdFactory, biasFactoryProfiles, activationFunctionFactoryProfiles, inputs, outputs, biases);
    }
}
