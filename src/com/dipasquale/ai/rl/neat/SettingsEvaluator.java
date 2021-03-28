package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.context.ContextDefault;
import com.dipasquale.ai.rl.neat.context.ContextDefaultComponentFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultConnectionGeneSupport;
import com.dipasquale.ai.rl.neat.context.ContextDefaultCrossOver;
import com.dipasquale.ai.rl.neat.context.ContextDefaultGeneralSupport;
import com.dipasquale.ai.rl.neat.context.ContextDefaultMutation;
import com.dipasquale.ai.rl.neat.context.ContextDefaultNeuralNetworkSupport;
import com.dipasquale.ai.rl.neat.context.ContextDefaultNodeGeneSupport;
import com.dipasquale.ai.rl.neat.context.ContextDefaultRandom;
import com.dipasquale.ai.rl.neat.context.ContextDefaultSpeciation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsEvaluator {
    private final SettingsGeneralEvaluatorSupport general;

    @Builder.Default
    private final SettingsNodeGeneSupport nodes = SettingsNodeGeneSupport.builder()
            .build();

    @Builder.Default
    private final SettingsConnectionGeneSupport connections = SettingsConnectionGeneSupport.builder()
            .build();

    @Builder.Default
    private final SettingsNeuralNetworkSupport neuralNetwork = SettingsNeuralNetworkSupport.builder()
            .build();

    @Builder.Default
    private final SettingsRandom random = SettingsRandom.builder()
            .build();

    @Builder.Default
    private final SettingsMutation mutation = SettingsMutation.builder()
            .build();

    @Builder.Default
    private final SettingsCrossOver crossOver = SettingsCrossOver.builder()
            .build();

    @Builder.Default
    private final SettingsSpeciation speciation = SettingsSpeciation.builder()
            .build();

    Context createContext() {
        ContextDefaultComponentFactory<ContextDefaultGeneralSupport> generalFactory = general.createFactory();
        ContextDefaultComponentFactory<ContextDefaultNodeGeneSupport> nodesFactory = nodes.createFactory(general.getGenomeFactory());
        ContextDefaultComponentFactory<ContextDefaultConnectionGeneSupport> connectionsFactory = connections.createFactory(neuralNetwork);
        ContextDefaultComponentFactory<ContextDefaultNeuralNetworkSupport> neuralNetworkFactory = neuralNetwork.createFactory();
        ContextDefaultComponentFactory<ContextDefaultRandom> randomFactory = random.createFactory();
        ContextDefaultComponentFactory<ContextDefaultMutation> mutationFactory = mutation.createFactory(random.isLessThanRandomSupport);
        ContextDefaultComponentFactory<ContextDefaultCrossOver> crossOverFactory = crossOver.createFactory(random.isLessThanRandomSupport);
        ContextDefaultComponentFactory<ContextDefaultSpeciation> speciationFactory = speciation.createFactory(general);

        return new ContextDefault(generalFactory, nodesFactory, connectionsFactory, neuralNetworkFactory, randomFactory, mutationFactory, crossOverFactory, speciationFactory);
    }
}
