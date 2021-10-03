package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.context.DefaultContextMetricSupport;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class MetricSupport {
    @Builder.Default
    private final EnumSet<MetricCollectionType> type = EnumSet.noneOf(MetricCollectionType.class);

    DefaultContextMetricSupport create(final ParallelismSupport parallelismSupport, final SpeciationSupport speciationSupport, final Map<RandomType, DualModeRandomSupport> randomSupports) {
        return DefaultContextMetricSupport.create(parallelismSupport, randomSupports, this, speciationSupport);
    }
}
