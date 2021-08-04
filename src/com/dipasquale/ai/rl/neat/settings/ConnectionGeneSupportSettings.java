package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.context.DefaultConnectionGeneSupportContext;
import com.dipasquale.ai.rl.neat.factory.WeightPerturber;
import com.dipasquale.ai.rl.neat.genotype.GenomeGenesisConnector;
import com.dipasquale.ai.rl.neat.switcher.factory.WeightPerturberSwitcher;
import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.switcher.ObjectSwitcher;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class ConnectionGeneSupportSettings {
    @Builder.Default
    private final FloatNumberSettings weightFactory = FloatNumberSettings.random(RandomType.UNIFORM, -1f, 1f);
    @Builder.Default
    private final FloatNumberSettings weightPerturber = FloatNumberSettings.literal(2.5f);

    private ObjectSwitcher<WeightPerturber> createWeightPerturberSwitcher(final ParallelismSupportSettings parallelism) {
        ObjectSwitcher<FloatFactory> floatFactorySwitcher = weightPerturber.createFactorySwitcher(parallelism);
        Pair<FloatFactory> floatFactoryPair = ObjectSwitcher.deconstruct(floatFactorySwitcher);

        return new WeightPerturberSwitcher(parallelism.isEnabled(), floatFactoryPair);
    }

    DefaultConnectionGeneSupportContext create(final GenesisGenomeTemplateSettings genesisGenomeTemplate, final NeuralNetworkSupportSettings neuralNetwork, final ParallelismSupportSettings parallelism) {
        boolean multipleRecurrentCyclesAllowed = neuralNetwork.getType() == NeuralNetworkType.MULTI_CYCLE_RECURRENT;
        ObjectSwitcher<FloatFactory> weightFactorySwitcher = weightFactory.createFactorySwitcher(parallelism);
        ObjectSwitcher<WeightPerturber> weightPerturberSwitcher = createWeightPerturberSwitcher(parallelism);
        GenomeGenesisConnector genomeGenesisConnector = genesisGenomeTemplate.createConnector(weightFactorySwitcher, parallelism);

        return new DefaultConnectionGeneSupportContext(multipleRecurrentCyclesAllowed, weightFactorySwitcher, weightPerturberSwitcher, genomeGenesisConnector);
    }
}
