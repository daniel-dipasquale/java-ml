package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.WeightFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefault;
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

    private FloatFactory createWeightSettings(final WeightFactory weightFactory) {
        if (initialWeightType == SettingsInitialWeightType.FIRST_RANDOM_SUBSEQUENT_COPY) {
            float weight = weightFactory.next();

            return () -> weight;
        }

        return weightFactory::next;
    }

    public GenomeDefaultFactory create(final ContextDefault context, final SettingsConnectionGeneSupport connections, final SettingsParallelism parallelism) {
        int inputsFixed = inputs.createFactory(parallelism).create();
        int outputsFixed = outputs.createFactory(parallelism).create();
        int biasesFixed = biases.size();
        SettingsGenomeFactoryNoConnections genomeFactoryNoConnections = new SettingsGenomeFactoryNoConnections(context, inputsFixed, outputsFixed, biasesFixed);
        FloatFactory weightFactory = createWeightSettings(connections.createWeightFactory(parallelism));

        return switch (initialConnectionType) {
            case ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS -> new SettingsGenomeFactoryAllToAllOutputs(context, genomeFactoryNoConnections, weightFactory, true);

            case ALL_INPUTS_TO_ALL_OUTPUTS -> new SettingsGenomeFactoryAllToAllOutputs(context, genomeFactoryNoConnections, weightFactory, false);

            default -> throw new IllegalStateException("SettingsInitialConnectionType.Random needs to be implemented");
        };
    }
}