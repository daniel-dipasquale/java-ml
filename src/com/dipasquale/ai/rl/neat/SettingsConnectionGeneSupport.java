package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.WeightPerturber;
import com.dipasquale.ai.rl.neat.context.ContextDefaultConnectionGeneSupport;
import com.dipasquale.ai.rl.neat.genotype.GenomeGenesisConnector;
import com.dipasquale.common.FloatFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SettingsConnectionGeneSupport {
    @Builder.Default
    private final SettingsFloatNumber weightFactory = SettingsFloatNumber.random(SettingsRandomType.UNIFORM, -1f, 1f);
    @Builder.Default
    private final SettingsFloatNumber weightPerturber = SettingsFloatNumber.literal(2.5f);

    ContextDefaultConnectionGeneSupport create(final SettingsGenesisGenomeTemplate genesisGenomeTemplate, final SettingsNeuralNetworkSupport neuralNetwork, final SettingsParallelismSupport parallelism) {
        boolean multipleRecurrentCyclesAllowed = neuralNetwork.getType() == SettingsNeuralNetworkType.MULTI_CYCLE_RECURRENT;
        FloatFactory weightFactoryFixed = weightFactory.createFactory(parallelism);
        WeightPerturber weightPerturberFixed = WeightPerturber.create(weightPerturber.createFactory(parallelism));
        GenomeGenesisConnector genomeGenesisConnector = genesisGenomeTemplate.createConnector(weightFactoryFixed);

        return new ContextDefaultConnectionGeneSupport(multipleRecurrentCyclesAllowed, weightFactoryFixed, weightPerturberFixed, genomeGenesisConnector);
    }
}
