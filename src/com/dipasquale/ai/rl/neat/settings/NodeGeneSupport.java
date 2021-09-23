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

    private static ObjectProfile<FloatFactory> createBiasFactoryProfile(final ParallelismSupport parallelismSupport, final List<FloatNumber> biases) {
        if (biases.isEmpty()) {
            IllegalStateFloatFactory floatFactory = new IllegalStateFloatFactory("there are no biases allowed in this genome");

            return new DefaultObjectProfile<>(parallelismSupport.isEnabled(), floatFactory);
        }

        Iterable<Pair<FloatFactory>> biasNodeBiasFactoryPairs = biases.stream()
                .map(sfn -> ObjectProfile.deconstruct(sfn.createFactoryProfile(parallelismSupport)))
                ::iterator;

        return new CyclicFloatFactoryProfile(parallelismSupport.isEnabled(), biasNodeBiasFactoryPairs);
    }

    private static ObjectProfile<ActivationFunctionFactory> createActivationFunctionFactoryProfile(final ParallelismSupport parallelismSupport, final ObjectProfile<EnumFactory<ActivationFunctionType>> activationFunctionTypeFactoryProfile) {
        Pair<EnumFactory<ActivationFunctionType>> activationFunctionTypeFactoryPair = ObjectProfile.deconstruct(activationFunctionTypeFactoryProfile);

        return new DefaultActivationFunctionFactoryProfile(parallelismSupport.isEnabled(), activationFunctionTypeFactoryPair);
    }

    private static ObjectProfile<ActivationFunctionFactory> createActivationFunctionFactoryProfile(final ParallelismSupport parallelismSupport, final ObjectProfile<EnumFactory<OutputActivationFunctionType>> outputActivationFunctionTypeFactoryProfile, final ObjectProfile<EnumFactory<ActivationFunctionType>> hiddenActivationFunctionTypeFactoryProfile) {
        Pair<EnumFactory<OutputActivationFunctionType>> outputActivationFunctionTypeFactoryPair = ObjectProfile.deconstruct(outputActivationFunctionTypeFactoryProfile);
        Pair<EnumFactory<ActivationFunctionType>> hiddenActivationFunctionTypeFactoryPair = ObjectProfile.deconstruct(hiddenActivationFunctionTypeFactoryProfile);

        return new OutputActivationFunctionFactoryProfile(parallelismSupport.isEnabled(), outputActivationFunctionTypeFactoryPair, hiddenActivationFunctionTypeFactoryPair);
    }

    DefaultContextNodeGeneSupport create(final GenesisGenomeTemplate genesisGenomeTemplate, final ParallelismSupport parallelismSupport) {
        DualModeNodeIdFactory nodeIdFactory = new DualModeNodeIdFactory(parallelismSupport.isEnabled());

        Map<NodeGeneType, ObjectProfile<FloatFactory>> biasFactoryProfiles = ImmutableMap.<NodeGeneType, ObjectProfile<FloatFactory>>builder()
                .put(NodeGeneType.INPUT, inputBias.createFactoryProfile(parallelismSupport))
                .put(NodeGeneType.OUTPUT, outputBias.createFactoryProfile(parallelismSupport))
                .put(NodeGeneType.BIAS, createBiasFactoryProfile(parallelismSupport, genesisGenomeTemplate.getBiases()))
                .put(NodeGeneType.HIDDEN, hiddenBias.createFactoryProfile(parallelismSupport))
                .build();

        ObjectProfile<EnumFactory<ActivationFunctionType>> inputActivationFunctionTypeFactoryProfile = inputActivationFunction.createFactoryProfile(parallelismSupport);
        ObjectProfile<EnumFactory<OutputActivationFunctionType>> outputActivationFunctionTypeFactoryProfile = outputActivationFunction.createFactoryProfile(parallelismSupport);
        ObjectProfile<EnumFactory<ActivationFunctionType>> hiddenActivationFunctionTypeFactoryProfile = hiddenActivationFunction.createFactoryProfile(parallelismSupport);

        Map<NodeGeneType, ObjectProfile<ActivationFunctionFactory>> activationFunctionFactoryProfiles = ImmutableMap.<NodeGeneType, ObjectProfile<ActivationFunctionFactory>>builder()
                .put(NodeGeneType.INPUT, createActivationFunctionFactoryProfile(parallelismSupport, inputActivationFunctionTypeFactoryProfile))
                .put(NodeGeneType.OUTPUT, createActivationFunctionFactoryProfile(parallelismSupport, outputActivationFunctionTypeFactoryProfile, hiddenActivationFunctionTypeFactoryProfile))
                .put(NodeGeneType.BIAS, new LiteralActivationFunctionFactoryProfile(parallelismSupport.isEnabled(), ActivationFunctionType.IDENTITY))
                .put(NodeGeneType.HIDDEN, createActivationFunctionFactoryProfile(parallelismSupport, hiddenActivationFunctionTypeFactoryProfile))
                .build();

        int inputs = genesisGenomeTemplate.getInputs().createFactoryProfile(parallelismSupport).getObject().create();
        int outputs = genesisGenomeTemplate.getOutputs().createFactoryProfile(parallelismSupport).getObject().create();
        int biases = genesisGenomeTemplate.getBiases().size();

        return new DefaultContextNodeGeneSupport(nodeIdFactory, biasFactoryProfiles, activationFunctionFactoryProfiles, inputs, outputs, biases);
    }
}
