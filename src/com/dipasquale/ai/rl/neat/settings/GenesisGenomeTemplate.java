package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.genotype.GenomeGenesisConnector;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.factory.LiteralFloatFactory;
import com.dipasquale.common.profile.DefaultObjectProfile;
import com.dipasquale.common.profile.ObjectProfile;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter(AccessLevel.PACKAGE)
public final class GenesisGenomeTemplate {
    private final IntegerNumber inputs;
    private final IntegerNumber outputs;
    @Builder.Default
    private final List<FloatNumber> biases = ImmutableList.of();
    @Builder.Default
    private final InitialConnectionType initialConnectionType = InitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS;
    @Builder.Default
    private final InitialWeightType initialWeightType = InitialWeightType.RANDOM;

    public static GenesisGenomeTemplate createDefault(final int inputs, final int outputs, final float[] bias) {
        return GenesisGenomeTemplate.builder()
                .inputs(IntegerNumber.literal(inputs))
                .outputs(IntegerNumber.literal(outputs))
                .biases(IntStream.range(0, bias.length)
                        .mapToObj(i -> FloatNumber.literal(bias[i]))
                        .collect(Collectors.toList()))
                .build();
    }

    public static GenesisGenomeTemplate createDefault(final int inputs, final int outputs) {
        return createDefault(inputs, outputs, new float[0]);
    }

    private ObjectProfile<FloatFactory> createWeightFactory(final ParallelismSupport parallelism, final ObjectProfile<FloatFactory> weightFactoryProfile) {
        if (initialWeightType == InitialWeightType.RANDOM) {
            return weightFactoryProfile;
        }

        float weight = weightFactoryProfile.getObject().create();
        LiteralFloatFactory weightFactory = new LiteralFloatFactory(weight);

        return new DefaultObjectProfile<>(parallelism.isEnabled(), weightFactory);
    }

    public GenomeGenesisConnector createConnector(final ParallelismSupport parallelism, final ObjectProfile<FloatFactory> weightFactory) {
        ObjectProfile<FloatFactory> weightFactoryProfile = createWeightFactory(parallelism, weightFactory);

        return switch (initialConnectionType) {
            case ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS -> new AllToAllOutputsGenesisGenomeConnector(weightFactoryProfile, true);

            case ALL_INPUTS_TO_ALL_OUTPUTS -> new AllToAllOutputsGenesisGenomeConnector(weightFactoryProfile, false);

            default -> {
                String message = String.format("%s needs to be implemented", initialConnectionType);

                throw new IllegalStateException(message);
            }
        };
    }
}