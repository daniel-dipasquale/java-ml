package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.fitness.FitnessControllerFactory;
import com.dipasquale.ai.common.fitness.LastValueFitnessControllerFactory;
import com.dipasquale.ai.rl.neat.factory.FitnessBucketProvider;
import com.dipasquale.ai.rl.neat.genotype.HistoricalMarkings;
import com.dipasquale.ai.rl.neat.phenotype.FeedForwardNeatNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivatorPool;
import com.dipasquale.ai.rl.neat.phenotype.GruNeatNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.IdentityNeuronLayerTopologyDefinition;
import com.dipasquale.ai.rl.neat.phenotype.LstmNeatNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.NeatNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.NeuronLayerTopologyDefinition;
import com.dipasquale.ai.rl.neat.phenotype.RecurrentNeatNeuralNetworkFactory;
import com.dipasquale.common.ArgumentValidatorSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class ActivationSettings {
    @Getter
    @Builder.Default
    private final GenesisGenomeTemplate genesisGenomeTemplate = null;
    @Getter
    @Builder.Default
    private final NeatEnvironment fitnessFunction = null;
    @Builder.Default
    private final FitnessControllerFactory fitnessControllerFactory = LastValueFitnessControllerFactory.getInstance();
    @Builder.Default
    private final NeuronLayerTopologyDefinition outputTopologyDefinition = IdentityNeuronLayerTopologyDefinition.getInstance();

    private static NeatNeuralNetworkFactory createNeuralNetworkFactory(final ConnectionGeneSettings connectionGeneSettings, final NeuronLayerTopologyDefinition outputTopologyDefinition) {
        float recurrentAllowanceRate = connectionGeneSettings.getRecurrentAllowanceRate();

        if (Float.compare(recurrentAllowanceRate, 0f) <= 0) {
            return new FeedForwardNeatNeuralNetworkFactory(outputTopologyDefinition);
        }

        return switch (connectionGeneSettings.getRecurrentStateType()) {
            case DEFAULT -> new RecurrentNeatNeuralNetworkFactory(outputTopologyDefinition);

            case LSTM -> new LstmNeatNeuralNetworkFactory(outputTopologyDefinition);

            case GRU -> new GruNeatNeuralNetworkFactory(outputTopologyDefinition);
        };
    }

    DefaultNeatContextActivationSupport create(final NeatInitializationContext initializationContext, final ConnectionGeneSettings connectionGeneSettings) {
        ArgumentValidatorSupport.ensureNotNull(genesisGenomeTemplate, "activation.genesisGenomeTemplate");
        ArgumentValidatorSupport.ensureNotNull(fitnessFunction, "activation.fitnessFunction");
        ArgumentValidatorSupport.ensureNotNull(fitnessControllerFactory, "activation.fitnessControllerFactory");
        ArgumentValidatorSupport.ensureNotNull(outputTopologyDefinition, "activation.outputTopologyDefinition");

        NeatNeuralNetworkFactory neuralNetworkFactory = createNeuralNetworkFactory(connectionGeneSettings, outputTopologyDefinition);
        GenomeActivatorPool genomeActivatorPool = new GenomeActivatorPool(neuralNetworkFactory);
        FitnessBucketProvider fitnessBucketProvider = new FitnessBucketProvider(fitnessControllerFactory);
        HistoricalMarkings historicalMarkings = initializationContext.getHistoricalMarkings();

        return switch (initializationContext.getEnvironmentType()) {
            case SECLUDED -> new DefaultNeatContextActivationSupport(genomeActivatorPool, (SecludedNeatEnvironment) fitnessFunction, fitnessBucketProvider, historicalMarkings);

            case COMMUNAL -> new DefaultNeatContextActivationSupport(genomeActivatorPool, (CommunalNeatEnvironment) fitnessFunction, fitnessBucketProvider, historicalMarkings);
        };
    }
}
