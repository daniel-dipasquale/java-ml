package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.common.fitness.FitnessDeterminerFactory;
import com.dipasquale.ai.common.fitness.LastValueFitnessDeterminerFactory;
import com.dipasquale.common.ArgumentValidatorSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class GeneralSupport {
    @Builder.Default
    private final IntegerNumber populationSize = IntegerNumber.literal(150);
    @Builder.Default
    private final GenesisGenomeTemplate genesisGenomeTemplate = null;
    @Builder.Default
    private final NeatEnvironment fitnessFunction = null;
    @Builder.Default
    private final FitnessDeterminerFactory fitnessDeterminerFactory = new LastValueFitnessDeterminerFactory();

    ContextObjectGeneralSupport create(final InitializationContext initializationContext) {
        int populationSizeFixed = populationSize.getSingletonValue(initializationContext);

        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(populationSizeFixed, 20, "populationSize");
        ArgumentValidatorSupport.ensureNotNull(genesisGenomeTemplate, "genesisGenomeTemplate");
        ArgumentValidatorSupport.ensureNotNull(fitnessFunction, "fitnessFunction");
        ArgumentValidatorSupport.ensureNotNull(fitnessDeterminerFactory, "fitnessDeterminerFactory");

        return ContextObjectGeneralSupport.create(populationSizeFixed);
    }
}
