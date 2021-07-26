package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.common.function.activation.OutputActivationFunctionType;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.genotype.GenomeGenesisConnector;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.factory.LiteralFloatFactory;
import com.dipasquale.common.switcher.DefaultObjectSwitcher;
import com.dipasquale.common.switcher.ObjectSwitcher;
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
public final class GenesisGenomeTemplateSettings {
    private final IntegerNumberSettings inputs;
    @Builder.Default
    private final FloatNumberSettings inputBias = FloatNumberSettings.literal(0f);
    @Builder.Default
    private final EnumSettings<ActivationFunctionType> inputActivationFunction = EnumSettings.literal(ActivationFunctionType.IDENTITY);
    private final IntegerNumberSettings outputs;
    @Builder.Default
    private final FloatNumberSettings outputBias = FloatNumberSettings.random(RandomType.UNIFORM, -1f, 1f);
    @Builder.Default
    private final EnumSettings<OutputActivationFunctionType> outputActivationFunction = EnumSettings.literal(OutputActivationFunctionType.SIGMOID);
    @Builder.Default
    private final List<FloatNumberSettings> biases = ImmutableList.of();
    @Builder.Default
    private final InitialConnectionType initialConnectionType = InitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS;
    @Builder.Default
    private final InitialWeightType initialWeightType = InitialWeightType.RANDOM;

    public static GenesisGenomeTemplateSettings createDefault(final int inputs, final int outputs, final float[] bias) {
        return GenesisGenomeTemplateSettings.builder()
                .inputs(IntegerNumberSettings.literal(inputs))
                .outputs(IntegerNumberSettings.literal(outputs))
                .biases(IntStream.range(0, bias.length)
                        .mapToObj(i -> FloatNumberSettings.literal(bias[i]))
                        .collect(Collectors.toList()))
                .build();
    }

    public static GenesisGenomeTemplateSettings createDefault(final int inputs, final int outputs) {
        return createDefault(inputs, outputs, new float[0]);
    }

    private ObjectSwitcher<FloatFactory> createWeightFactory(final ObjectSwitcher<FloatFactory> weightFactorySwitcher, final ParallelismSupportSettings parallelism) {
        if (initialWeightType == InitialWeightType.RANDOM) {
            return weightFactorySwitcher;
        }

        float weight = weightFactorySwitcher.getObject().create();
        LiteralFloatFactory weightFactory = new LiteralFloatFactory(weight);

        return new DefaultObjectSwitcher<>(parallelism.isEnabled(), weightFactory);
    }

    public GenomeGenesisConnector createConnector(final ObjectSwitcher<FloatFactory> weightFactory, final ParallelismSupportSettings parallelism) {
        ObjectSwitcher<FloatFactory> weightFactorySwitcher = createWeightFactory(weightFactory, parallelism);

        return switch (initialConnectionType) {
            case ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS -> new AllToAllOutputsGenesisGenomeConnector(weightFactorySwitcher, true);

            case ALL_INPUTS_TO_ALL_OUTPUTS -> new AllToAllOutputsGenesisGenomeConnector(weightFactorySwitcher, false);

            default -> {
                String message = String.format("%s needs to be implemented", initialConnectionType);

                throw new IllegalStateException(message);
            }
        };
    }
}