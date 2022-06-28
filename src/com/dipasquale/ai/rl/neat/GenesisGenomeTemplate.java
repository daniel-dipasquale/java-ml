package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.genotype.FullyConnectedGenesisGenomeConnector;
import com.dipasquale.ai.rl.neat.genotype.GenesisGenomeConnector;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    private FloatNumber.DualModeFactory createWeightFactory(final InitializationContext initializationContext, final FloatNumber.DualModeFactory weightFactory) {
        return switch (initialWeightType) {
            case ALL_RANDOM -> weightFactory;

            case ONCE_RANDOM_REST_CARBON_COPY -> {
                float weight = weightFactory.create();

                yield FloatNumber.literal(weight).createFactory(initializationContext);
            }
        };
    }

    public GenesisGenomeConnector createConnector(final InitializationContext initializationContext, final FloatNumber.DualModeFactory weightFactory) {
        FloatNumber.DualModeFactory fixedWeightFactory = createWeightFactory(initializationContext, weightFactory);

        return switch (initialConnectionType) {
            case FULLY_CONNECTED -> new FullyConnectedGenesisGenomeConnector(hiddenLayers, fixedWeightFactory, true);

            case FULL_CONNECTED_EXCLUDING_BIAS -> new FullyConnectedGenesisGenomeConnector(hiddenLayers, fixedWeightFactory, false);
        };
    }
}