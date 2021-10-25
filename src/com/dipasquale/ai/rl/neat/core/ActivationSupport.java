package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.context.DefaultContextActivationSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class ActivationSupport {
    DefaultContextActivationSupport create(final InitializationContext initializationContext, final GeneralEvaluatorSupport generalEvaluatorSupport, final ConnectionGeneSupport connectionGeneSupport) {
        return DefaultContextActivationSupport.create(initializationContext, generalEvaluatorSupport, connectionGeneSupport);
    }
}
