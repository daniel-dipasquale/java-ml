package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.phenotype.IdentityNeuronLayerTopologyDefinition;
import com.dipasquale.ai.rl.neat.phenotype.NeuronLayerTopologyDefinition;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class ActivationSettings {
    @Builder.Default
    private final NeuronLayerTopologyDefinition outputTopologyDefinition = IdentityNeuronLayerTopologyDefinition.getInstance();

    ContextObjectActivationSupport create(final InitializationContext initializationContext, final ConnectionGeneSettings connectionGeneSettings) {
        return ContextObjectActivationSupport.create(initializationContext, connectionGeneSettings, this);
    }
}
