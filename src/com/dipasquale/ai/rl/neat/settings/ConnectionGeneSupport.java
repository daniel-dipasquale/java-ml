package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.context.DefaultContextConnectionGeneParameters;
import com.dipasquale.ai.rl.neat.context.DefaultContextConnectionGeneSupport;
import com.dipasquale.ai.rl.neat.factory.WeightPerturber;
import com.dipasquale.ai.rl.neat.genotype.GenomeGenesisConnector;
import com.dipasquale.ai.rl.neat.profile.factory.WeightPerturberProfile;
import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.profile.ObjectProfile;
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

    private ObjectProfile<WeightPerturber> createWeightPerturberProfile(final ParallelismSupport parallelism) {
        ObjectProfile<FloatFactory> floatFactoryProfile = weightPerturber.createFactoryProfile(parallelism);
        Pair<FloatFactory> floatFactoryPair = ObjectProfile.deconstruct(floatFactoryProfile);

        return new WeightPerturberProfile(parallelism.isEnabled(), floatFactoryPair);
    }

    DefaultContextConnectionGeneSupport create(final GenesisGenomeTemplate genesisGenomeTemplate, final NeuralNetworkSupport neuralNetwork, final ParallelismSupport parallelism) {
        DefaultContextConnectionGeneParameters params = DefaultContextConnectionGeneParameters.builder()
                .multipleRecurrentCyclesAllowed(neuralNetwork.getType() == NeuralNetworkType.MULTI_CYCLE_RECURRENT)
                .build();

        ObjectProfile<FloatFactory> weightFactoryProfile = weightFactory.createFactoryProfile(parallelism);
        ObjectProfile<WeightPerturber> weightPerturberProfile = createWeightPerturberProfile(parallelism);
        GenomeGenesisConnector genomeGenesisConnector = genesisGenomeTemplate.createConnector(parallelism, weightFactoryProfile);

        return new DefaultContextConnectionGeneSupport(params, weightFactoryProfile, weightPerturberProfile, genomeGenesisConnector);
    }
}
