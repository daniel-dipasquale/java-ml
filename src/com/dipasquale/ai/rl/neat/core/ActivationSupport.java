package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.phenotype.IdentityNeuronLayerNormalizer;
import com.dipasquale.ai.rl.neat.phenotype.NeuronLayerNormalizer;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class ActivationSupport {
    @Builder.Default
    private final NeuronLayerNormalizer outputLayerNormalizer = new IdentityNeuronLayerNormalizer();

    ContextObjectActivationSupport create(final InitializationContext initializationContext, final GeneralSupport generalSupport, final ConnectionGeneSupport connectionGeneSupport) {
        return ContextObjectActivationSupport.create(initializationContext, generalSupport, connectionGeneSupport, this);
    }
}
