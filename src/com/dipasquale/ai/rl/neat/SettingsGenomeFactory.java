package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.genotype.GenomeDefaultFactory;
import com.dipasquale.common.FloatFactory;
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
public final class SettingsGenomeFactory {
    private static final SettingsGenomeFactoryNoConnections GENOME_FACTORY_NO_CONNECTIONS = new SettingsGenomeFactoryNoConnections();
    private final SettingsIntegerNumber inputs;
    @Builder.Default
    private final SettingsFloatNumber inputBias = SettingsFloatNumber.literal(0f);
    @Builder.Default
    private final SettingsEnum<SettingsActivationFunction> inputActivationFunction = SettingsEnum.literal(SettingsActivationFunction.IDENTITY);
    private final SettingsIntegerNumber outputs;
    @Builder.Default
    private final SettingsFloatNumber outputBias = SettingsFloatNumber.random(SettingsRandomType.UNIFORM, -1f, 1f);
    @Builder.Default
    private final SettingsEnum<SettingsOutputActivationFunction> outputActivationFunction = SettingsEnum.literal(SettingsOutputActivationFunction.COPY_FROM_HIDDEN);
    @Builder.Default
    private final List<SettingsFloatNumber> biases = ImmutableList.of();
    @Builder.Default
    private final SettingsInitialConnectionType initialConnectionType = SettingsInitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS;
    @Builder.Default
    private final SettingsInitialWeightType initialWeightType = SettingsInitialWeightType.RANDOM;

    public static SettingsGenomeFactory createDefault(final int inputs, final int outputs, final float[] bias) {
        return SettingsGenomeFactory.builder()
                .inputs(SettingsIntegerNumber.literal(inputs))
                .outputs(SettingsIntegerNumber.literal(outputs))
                .biases(IntStream.range(0, bias.length)
                        .mapToObj(i -> SettingsFloatNumber.literal(bias[i]))
                        .collect(Collectors.toList()))
                .build();
    }

    public static SettingsGenomeFactory createDefault(final int inputs, final int outputs) {
        return createDefault(inputs, outputs, new float[0]);
    }

    private FloatFactory createWeightSettings(final FloatFactory weightFactory) {
        if (initialWeightType == SettingsInitialWeightType.RANDOM) {
            return weightFactory;
        }

        return FloatFactory.createLiteral(weightFactory.create());
    }

    public GenomeDefaultFactory create(final SettingsConnectionGeneSupport connections, final SettingsParallelism parallelism) {
        FloatFactory weightFactory = createWeightSettings(connections.createWeightFactory(parallelism));

        return switch (initialConnectionType) {
            case ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS -> new SettingsGenomeFactoryAllToAllOutputs(GENOME_FACTORY_NO_CONNECTIONS, weightFactory, true);

            case ALL_INPUTS_TO_ALL_OUTPUTS -> new SettingsGenomeFactoryAllToAllOutputs(GENOME_FACTORY_NO_CONNECTIONS, weightFactory, false);

            default -> throw new IllegalStateException("SettingsInitialConnectionType.Random needs to be implemented");
        };
    }
}