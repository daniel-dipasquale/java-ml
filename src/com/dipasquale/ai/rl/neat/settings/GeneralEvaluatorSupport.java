package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.common.fitness.FitnessDeterminerFactory;
import com.dipasquale.ai.common.fitness.LastValueFitnessDeterminerFactory;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.context.DefaultContextGeneralSupport;
import com.dipasquale.ai.rl.neat.core.NeatEnvironment;
import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class GeneralEvaluatorSupport {
    @Builder.Default
    private final IntegerNumber populationSize = IntegerNumber.literal(150);
    @Builder.Default
    private final GenesisGenomeTemplate genesisGenomeTemplate = null;
    @Builder.Default
    private final NeatEnvironment fitnessFunction = null;
    @Builder.Default
    private final FitnessDeterminerFactory fitnessDeterminerFactory = new LastValueFitnessDeterminerFactory();

    DefaultContextGeneralSupport create(final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports) {
        int populationSizeFixed = populationSize.getSingletonValue(parallelismSupport, randomSupports);

        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(populationSizeFixed, 20, "populationSize");
        ArgumentValidatorSupport.ensureNotNull(genesisGenomeTemplate, "genesisGenomeTemplate");
        ArgumentValidatorSupport.ensureNotNull(fitnessFunction, "fitnessFunction");
        ArgumentValidatorSupport.ensureNotNull(fitnessDeterminerFactory, "fitnessDeterminerFactory");

        return DefaultContextGeneralSupport.create(populationSizeFixed);
    }
}
