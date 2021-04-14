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
    @Builder.Default
    private final int populationSize = 150;
    @Builder.Default
    private final SettingsGenesisGenomeTemplate genesisGenomeConnector = SettingsGenesisGenomeTemplate.createDefault(1, 1);
    @Builder.Default
    private final FitnessDeterminerFactory fitnessDeterminerFactory = FitnessDeterminerFactory.createLastValueFactory();
    @Builder.Default
    private final NeatEnvironment environment = g -> 0f;

    ContextDefaultGeneralSupport create() {
        ArgumentValidatorUtils.ensureGreaterThanOrEqualTo(populationSize, 20, "populationSize");

        return new ContextDefaultGeneralSupport(populationSize, fitnessDeterminerFactory, environment);
    }
}
