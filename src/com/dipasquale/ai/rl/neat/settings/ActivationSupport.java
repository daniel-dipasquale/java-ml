package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.context.DefaultContextActivationSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class ActivationSupport {
    @Builder.Default
    private final NeuralNetworkType neuralNetworkType = NeuralNetworkType.MULTI_CYCLE_RECURRENT;

    DefaultContextActivationSupport create(final GeneralEvaluatorSupport generalEvaluatorSupport, final ParallelismSupport parallelismSupport) {
        return DefaultContextActivationSupport.create(parallelismSupport, generalEvaluatorSupport, this);
    }
}
