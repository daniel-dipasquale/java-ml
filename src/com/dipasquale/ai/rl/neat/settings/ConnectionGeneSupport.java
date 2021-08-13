/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

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
public final class ConnectionGeneSupport {
    @Builder.Default
    private final FloatNumber weightFactory = FloatNumber.random(RandomType.UNIFORM, -1f, 1f);
    @Builder.Default
    private final FloatNumber weightPerturber = FloatNumber.literal(2.5f);

    private ObjectSwitcher<WeightPerturber> createWeightPerturberSwitcher(final ParallelismSupport parallelism) {
        ObjectSwitcher<FloatFactory> floatFactorySwitcher = weightPerturber.createFactorySwitcher(parallelism);
        Pair<FloatFactory> floatFactoryPair = ObjectSwitcher.deconstruct(floatFactorySwitcher);

        return new WeightPerturberSwitcher(parallelism.isEnabled(), floatFactoryPair);
    }

    DefaultConnectionGeneSupportContext create(final GenesisGenomeTemplate genesisGenomeTemplate, final NeuralNetworkSupport neuralNetwork, final ParallelismSupport parallelism) {
        boolean multipleRecurrentCyclesAllowed = neuralNetwork.getType() == NeuralNetworkType.MULTI_CYCLE_RECURRENT;
        ObjectSwitcher<FloatFactory> weightFactorySwitcher = weightFactory.createFactorySwitcher(parallelism);
        ObjectSwitcher<WeightPerturber> weightPerturberSwitcher = createWeightPerturberSwitcher(parallelism);
        GenomeGenesisConnector genomeGenesisConnector = genesisGenomeTemplate.createConnector(weightFactorySwitcher, parallelism);

        return new DefaultConnectionGeneSupportContext(multipleRecurrentCyclesAllowed, weightFactorySwitcher, weightPerturberSwitcher, genomeGenesisConnector);
    }
}
