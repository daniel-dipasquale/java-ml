package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.context.DefaultContextActivationSupport;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class ActivationSupport {
    DefaultContextActivationSupport create(final GeneralEvaluatorSupport generalEvaluatorSupport, final ConnectionGeneSupport connectionGeneSupport, final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports) {
        return DefaultContextActivationSupport.create(parallelismSupport, randomSupports, generalEvaluatorSupport, connectionGeneSupport);
    }
}
