package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.genotype.FullyConnectedGenesisGenomeConnector;
import com.dipasquale.ai.rl.neat.genotype.GenesisGenomeConnector;
import com.dipasquale.common.factory.ConstantFloatFactory;
import com.dipasquale.common.factory.FloatFactory;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class GenesisGenomeTemplate {
    private final int inputs;
    private final int outputs;
    @Builder.Default
    private final List<Float> biases = List.of();
    @Builder.Default
    private final List<Integer> hiddenLayers = List.of();
    @Builder.Default
    private final InitialConnectionType initialConnectionType = InitialConnectionType.FULLY_CONNECTED;
    @Builder.Default
    private final InitialWeightType initialWeightType = InitialWeightType.ALL_RANDOM;

    private FloatFactory createWeightFactory(final FloatFactory weightFactory) {
        return switch (initialWeightType) {
            case ONCE_RANDOM_REST_CARBON_COPY -> {
                float weight = weightFactory.create();

                yield new ConstantFloatFactory(weight);
            }

            case ALL_RANDOM -> weightFactory;
        };
    }

    public GenesisGenomeConnector createConnector(final FloatFactory weightFactory) {
        FloatFactory fixedWeightFactory = createWeightFactory(weightFactory);

        return switch (initialConnectionType) {
            case FULL_CONNECTED_EXCLUDING_BIAS -> new FullyConnectedGenesisGenomeConnector(hiddenLayers, fixedWeightFactory, false);

            case FULLY_CONNECTED -> new FullyConnectedGenesisGenomeConnector(hiddenLayers, fixedWeightFactory, true);
        };
    }
}