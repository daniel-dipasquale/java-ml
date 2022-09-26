package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.factory.RandomSupportFactory;
import com.dipasquale.ai.rl.neat.genotype.HistoricalMarkings;
import com.dipasquale.common.random.RandomSupport;
import lombok.Getter;

import java.util.Set;

final class NeatInitializationContext {
    private static final RandomSupportFactory RANDOM_SUPPORT_FACTORY = RandomSupportFactory.getInstance();
    @Getter
    private final Set<Long> threadIds;
    private final RandomnessSettings randomnessSettings;
    @Getter
    private final NeatEnvironmentType environmentType;
    @Getter
    private final HistoricalMarkings historicalMarkings;

    NeatInitializationContext(final ParallelismSettings parallelismSettings, final RandomnessSettings randomnessSettings, final ActivationSettings activationSettings) {
        this.threadIds = parallelismSettings.extractThreadIds();
        this.randomnessSettings = randomnessSettings;
        this.environmentType = NeatEnvironmentType.from(activationSettings.getFitnessFunction());
        this.historicalMarkings = new HistoricalMarkings();
    }

    public RandomSupport createRandomSupport(final RandomType randomType) {
        return RANDOM_SUPPORT_FACTORY.create(randomType);
    }

    public RandomSupport createDefaultRandomSupport() {
        return createRandomSupport(randomnessSettings.getType());
    }
}
