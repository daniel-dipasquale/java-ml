package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.common.fitness.FitnessDeterminerFactory;
import com.dipasquale.ai.rl.neat.context.DefaultGeneralSupportContext;
import com.dipasquale.ai.rl.neat.core.NeatEnvironment;
import com.dipasquale.common.ArgumentValidatorSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter(AccessLevel.PACKAGE)
public final class GeneralEvaluatorSupportSettings {
    @Builder.Default
    private final int populationSize = 150;
    @Builder.Default
    private final GenesisGenomeTemplateSettings genesisGenomeFactory = null;
    @Builder.Default
    private final FitnessDeterminerFactory fitnessDeterminerFactory = FitnessDeterminerFactory.createLastValue();
    @Builder.Default
    private final NeatEnvironment fitnessFunction = null;

    DefaultGeneralSupportContext create() {
        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(populationSize, 20, "populationSize");
        ArgumentValidatorSupport.ensureNotNull(genesisGenomeFactory, "genesisGenomeFactory");
        ArgumentValidatorSupport.ensureNotNull(fitnessDeterminerFactory, "fitnessDeterminerFactory");
        ArgumentValidatorSupport.ensureNotNull(fitnessFunction, "fitnessFunction");

        return new DefaultGeneralSupportContext(populationSize, fitnessDeterminerFactory, fitnessFunction);
    }
}