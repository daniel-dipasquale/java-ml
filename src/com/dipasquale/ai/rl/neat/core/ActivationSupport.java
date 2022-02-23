package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.phenotype.IdentityNeuronLayerTopologyDefinition;
import com.dipasquale.ai.rl.neat.phenotype.NeuronLayerTopologyDefinition;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class ActivationSupport {
    @Builder.Default
    private final NeuronLayerTopologyDefinition outputTopologyDefinition = IdentityNeuronLayerTopologyDefinition.getInstance();

    ContextObjectActivationSupport create(final InitializationContext initializationContext, final GeneralSupport generalSupport, final ConnectionGeneSupport connectionGeneSupport) {
        return ContextObjectActivationSupport.create(initializationContext, generalSupport, connectionGeneSupport, this);
    }
}
