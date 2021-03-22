package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.ContextDefault;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefaultFactory;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class SettingsGenomeFactory {
    private final SettingsIntegerNumber inputs;
    @Builder.Default
    private final SettingsFloatNumber inputBias = SettingsFloatNumber.literal(0f);
    @Builder.Default
    private final SettingsEnum<SettingsActivationFunction> inputActivationFunction = SettingsEnum.literal(SettingsActivationFunction.Identity);
    private final SettingsIntegerNumber outputs;
    private final SettingsFloatNumber outputBias = SettingsFloatNumber.literal(0f);
    @Builder.Default
    private final SettingsEnum<SettingsOutputActivationFunction> outputActivationFunction = SettingsEnum.literal(SettingsOutputActivationFunction.CopyFromHidden);
    @Builder.Default
    private final List<SettingsFloatNumber> biases = ImmutableList.of();
    @Builder.Default
    private final SettingsInitialConnectionType initialConnectionType = SettingsInitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS;
    @Builder.Default
    private final SettingsInitialWeightType initialWeightType = SettingsInitialWeightType.Random;

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

    private SettingsFloatNumber createWeightSettings(final ContextDefault context) {
        if (initialWeightType == SettingsInitialWeightType.FirstRandomSubsequentCopy) {
            return SettingsFloatNumber.literal(context.connections().nextWeight());
        }

        return SettingsFloatNumber.strategy(() -> context.connections().nextWeight());
    }

    public GenomeDefaultFactory create(final ContextDefault context) {
        SettingsGenomeFactoryNoConnections genomeFactoryNoConnections = new SettingsGenomeFactoryNoConnections(context, inputs.get(), outputs.get(), biases.size());

        return switch (initialConnectionType) {
            case ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS -> new SettingsGenomeFactoryAllToAllOutputs(context, genomeFactoryNoConnections, createWeightSettings(context), true);

            case ALL_INPUTS_TO_ALL_OUTPUTS -> new SettingsGenomeFactoryAllToAllOutputs(context, genomeFactoryNoConnections, createWeightSettings(context), false);

            default -> throw new IllegalStateException("InitialConnectionType.RANDOM needs to be implemented");
        };
    }
}