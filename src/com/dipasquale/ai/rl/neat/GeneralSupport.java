package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.fitness.FitnessControllerFactory;
import com.dipasquale.ai.common.fitness.LastValueFitnessControllerFactory;
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
    private final int populationSize = 150;
    @Builder.Default
    private final GenesisGenomeTemplate genesisGenomeTemplate = null;
    @Builder.Default
    private final NeatEnvironment fitnessFunction = null;
    @Builder.Default
    private final FitnessControllerFactory fitnessControllerFactory = LastValueFitnessControllerFactory.getInstance();

    ContextObjectGeneralSupport create(final InitializationContext initializationContext) {
        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(populationSize, 20, "populationSize");
        ArgumentValidatorSupport.ensureNotNull(genesisGenomeTemplate, "genesisGenomeTemplate");
        ArgumentValidatorSupport.ensureNotNull(fitnessFunction, "fitnessFunction");
        ArgumentValidatorSupport.ensureNotNull(fitnessControllerFactory, "fitnessControllerFactory");

        return ContextObjectGeneralSupport.create(populationSize);
    }
}
