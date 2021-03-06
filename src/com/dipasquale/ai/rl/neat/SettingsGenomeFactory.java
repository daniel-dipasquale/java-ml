package com.dipasquale.ai.rl.neat;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class SettingsGenomeFactory {
    private final SettingsIntegerNumber inputs;
    @Builder.Default
    private final SettingsFloatNumber inputBias = SettingsFloatNumber.literal(0f);
    @Builder.Default
    private final SettingsActivationFunction inputActivationFunction = SettingsActivationFunction.Identity;
    private final SettingsIntegerNumber outputs;
    private final SettingsFloatNumber outputBias = SettingsFloatNumber.literal(0f);
    @Builder.Default
    private final SettingsActivationFunction outputActivationFunction = SettingsActivationFunction.Identity;
    @Builder.Default
    private final List<SettingsFloatNumber> biases = ImmutableList.of();
    @Builder.Default
    private final SettingsInitialConnectionType initialConnectionType = SettingsInitialConnectionType.ALL_TO_ALL_OUTPUTS;
    @Builder.Default
    private final SettingsInitialWeightType initialWeightType = SettingsInitialWeightType.RANDOM;

    public static SettingsGenomeFactory createDefault(final int inputs, final int outputs) {
        return SettingsGenomeFactory.builder()
                .inputs(SettingsIntegerNumber.literal(inputs))
                .outputs(SettingsIntegerNumber.literal(outputs))
                .build();
    }

    private <T extends Comparable<T>> SettingsFloatNumber createWeightSettings(final ContextDefault<T> context) {
        if (initialWeightType == SettingsInitialWeightType.SAME) {
            return SettingsFloatNumber.literal(context.connections().nextWeight());
        }

        return SettingsFloatNumber.strategy(() -> context.connections().nextWeight());
    }

    public <T extends Comparable<T>> GenomeDefaultFactory<T> create(final ContextDefault<T> context) {
        SettingsGenomeFactoryNoConnections<T> genomeFactoryNoConnections = new SettingsGenomeFactoryNoConnections<>(context, inputs.get(), outputs.get(), biases.size());

        return switch (initialConnectionType) {
            case ALL_TO_ALL_OUTPUTS -> new SettingsGenomeFactoryAllToAllOutputs<>(context, genomeFactoryNoConnections, createWeightSettings(context), true);

            case ALL_INPUTS_TO_ALL_OUTPUTS -> new SettingsGenomeFactoryAllToAllOutputs<>(context, genomeFactoryNoConnections, createWeightSettings(context), false);

            default -> throw new IllegalStateException("InitialConnectionType.RANDOM needs to be implemented");
        };
    }
}