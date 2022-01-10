package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.genotype.AllToAllOutputsGenesisGenomeConnector;
import com.dipasquale.ai.rl.neat.genotype.GenesisGenomeConnector;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class GenesisGenomeTemplate {
    private final IntegerNumber inputs;
    private final IntegerNumber outputs;
    @Builder.Default
    private final List<FloatNumber> biases = List.of();
    @Builder.Default
    private final InitialConnectionType initialConnectionType = InitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS;
    @Builder.Default
    private final InitialWeightType initialWeightType = InitialWeightType.ALL_RANDOM;

    public static GenesisGenomeTemplate createDefault(final int inputs, final int outputs, final float[] bias) {
        return GenesisGenomeTemplate.builder()
                .inputs(IntegerNumber.literal(inputs))
                .outputs(IntegerNumber.literal(outputs))
                .biases(IntStream.range(0, bias.length)
                        .mapToObj(index -> FloatNumber.literal(bias[index]))
                        .collect(Collectors.toList()))
                .build();
    }

    public static GenesisGenomeTemplate createDefault(final int inputs, final int outputs) {
        return createDefault(inputs, outputs, new float[0]);
    }

    private FloatNumber.DualModeFactory createWeightFactory(final InitializationContext initializationContext, final FloatNumber.DualModeFactory weightFactory) {
        if (initialWeightType == InitialWeightType.ALL_RANDOM) {
            return weightFactory;
        }

        float weight = weightFactory.create();

        return FloatNumber.literal(weight).createFactory(initializationContext);
    }

    public GenesisGenomeConnector createConnector(final InitializationContext initializationContext, final FloatNumber.DualModeFactory weightFactory) {
        FloatNumber.DualModeFactory weightFactoryFixed = createWeightFactory(initializationContext, weightFactory);

        return switch (initialConnectionType) {
            case ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS -> new AllToAllOutputsGenesisGenomeConnector(weightFactoryFixed, true);

            case ALL_INPUTS_TO_ALL_OUTPUTS -> new AllToAllOutputsGenesisGenomeConnector(weightFactoryFixed, false);

            default -> {
                String message = String.format("%s needs to be implemented", initialConnectionType);

                throw new UnsupportedOperationException(message);
            }
        };
    }
}