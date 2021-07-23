package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.genotype.GenomeGenesisConnector;
import com.dipasquale.common.concurrent.FloatBiFactory;
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
public final class SettingsGenesisGenomeTemplate {
    private final SettingsIntegerNumber inputs;
    @Builder.Default
    private final SettingsFloatNumber inputBias = SettingsFloatNumber.literal(0f);
    @Builder.Default
    private final SettingsEnum<SettingsActivationFunction> inputActivationFunction = SettingsEnum.literal(SettingsActivationFunction.IDENTITY);
    private final SettingsIntegerNumber outputs;
    @Builder.Default
    private final SettingsFloatNumber outputBias = SettingsFloatNumber.random(SettingsRandomType.UNIFORM, -1f, 1f);
    @Builder.Default
    private final SettingsEnum<SettingsOutputActivationFunction> outputActivationFunction = SettingsEnum.literal(SettingsOutputActivationFunction.SIGMOID);
    @Builder.Default
    private final List<SettingsFloatNumber> biases = ImmutableList.of();
    @Builder.Default
    private final SettingsInitialConnectionType initialConnectionType = SettingsInitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS;
    @Builder.Default
    private final SettingsInitialWeightType initialWeightType = SettingsInitialWeightType.RANDOM;

    public static SettingsGenesisGenomeTemplate createDefault(final int inputs, final int outputs, final float[] bias) {
        return SettingsGenesisGenomeTemplate.builder()
                .inputs(SettingsIntegerNumber.literal(inputs))
                .outputs(SettingsIntegerNumber.literal(outputs))
                .biases(IntStream.range(0, bias.length)
                        .mapToObj(i -> SettingsFloatNumber.literal(bias[i]))
                        .collect(Collectors.toList()))
                .build();
    }

    public static SettingsGenesisGenomeTemplate createDefault(final int inputs, final int outputs) {
        return createDefault(inputs, outputs, new float[0]);
    }

    private FloatBiFactory createWeightSettings(final FloatBiFactory weightFactory) {
        if (initialWeightType == SettingsInitialWeightType.RANDOM) {
            return weightFactory;
        }

        return FloatBiFactory.createLiteral(weightFactory.create());
    }

    public GenomeGenesisConnector createConnector(final FloatBiFactory weightFactor) {
        FloatBiFactory weightFactory = createWeightSettings(weightFactor);

        return switch (initialConnectionType) {
            case ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS -> new SettingsGenesisGenomeConnectorAllToAllOutputs(weightFactory, true);

            case ALL_INPUTS_TO_ALL_OUTPUTS -> new SettingsGenesisGenomeConnectorAllToAllOutputs(weightFactory, false);

            default -> throw new IllegalStateException("SettingsInitialConnectionType.Random needs to be implemented");
        };
    }
}