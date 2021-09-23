package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.common.factory.WeightPerturber;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.context.DefaultContextConnectionGeneParameters;
import com.dipasquale.ai.rl.neat.context.DefaultContextConnectionGeneSupport;
import com.dipasquale.ai.rl.neat.genotype.GenesisGenomeConnector;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype.DualModeHistoricalMarkings;
import com.dipasquale.ai.rl.neat.synchronization.dual.profile.factory.WeightPerturberProfile;
import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class ConnectionGeneSupport {
    @Builder.Default
    private final FloatNumber weightFactory = FloatNumber.random(RandomType.UNIFORM, -1f, 1f);
    @Builder.Default
    private final FloatNumber weightPerturber = FloatNumber.literal(2.5f);

    private ObjectProfile<WeightPerturber> createWeightPerturberProfile(final ParallelismSupport parallelismSupport) {
        ObjectProfile<FloatFactory> floatFactoryProfile = weightPerturber.createFactoryProfile(parallelismSupport);
        Pair<FloatFactory> floatFactoryPair = ObjectProfile.deconstruct(floatFactoryProfile);

        return new WeightPerturberProfile(parallelismSupport.isEnabled(), floatFactoryPair);
    }

    DefaultContextConnectionGeneSupport create(final GenesisGenomeTemplate genesisGenomeTemplate, final ActivationSupport activationSupport, final ParallelismSupport parallelismSupport) {
        DefaultContextConnectionGeneParameters params = DefaultContextConnectionGeneParameters.builder()
                .multipleRecurrentCyclesAllowed(activationSupport.getNeuralNetworkType() == NeuralNetworkType.MULTI_CYCLE_RECURRENT)
                .build();

        ObjectProfile<FloatFactory> weightFactoryProfile = weightFactory.createFactoryProfile(parallelismSupport);
        ObjectProfile<WeightPerturber> weightPerturberProfile = createWeightPerturberProfile(parallelismSupport);
        GenesisGenomeConnector genesisGenomeConnector = genesisGenomeTemplate.createConnector(parallelismSupport, weightFactoryProfile);
        DualModeHistoricalMarkings historicalMarkings = new DualModeHistoricalMarkings(parallelismSupport.isEnabled(), parallelismSupport.getNumberOfThreads());

        return new DefaultContextConnectionGeneSupport(params, weightFactoryProfile, weightPerturberProfile, genesisGenomeConnector, historicalMarkings);
    }
}
