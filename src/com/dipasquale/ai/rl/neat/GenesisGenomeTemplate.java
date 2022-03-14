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
public final class GenesisGenomeTemplate { // TODO: allow experiments where the genesis genome allows hidden nodes (in the form of fully connected layers)
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
        FloatNumber.DualModeFactory weightFactoryFixed = createWeightFactory(initializationContext, weightFactory);

        return switch (initialConnectionType) {
            case FULLY_CONNECTED -> new FullyConnectedGenesisGenomeConnector(hiddenLayers, weightFactoryFixed, true);

            case FULL_CONNECTED_EXCLUDING_BIAS -> new FullyConnectedGenesisGenomeConnector(hiddenLayers, weightFactoryFixed, false);
        };
    }
}