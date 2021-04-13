package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.FitnessDeterminerFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultGeneralSupport;
import com.dipasquale.common.ArgumentValidatorUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter(AccessLevel.PACKAGE)
public final class SettingsGeneralEvaluatorSupport {
    private final int populationSize;
    private final SettingsGenesisGenomeTemplate genesisGenomeConnector;
    private final FitnessDeterminerFactory fitnessDeterminerFactory;
    private final NeatEnvironment environment;

    ContextDefaultGeneralSupport create(final SettingsConnectionGeneSupport connections, final SettingsParallelismSupport parallelism) {
        ArgumentValidatorUtils.ensureGreaterThanOrEqualTo(populationSize, 20, "populationSize");

        return new ContextDefaultGeneralSupport(populationSize, fitnessDeterminerFactory, environment);
    }
}
