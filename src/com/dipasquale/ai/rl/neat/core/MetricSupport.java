package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.context.DefaultContextMetricSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.EnumSet;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class MetricSupport {
    @Builder.Default
    private final EnumSet<MetricCollectionType> type = EnumSet.noneOf(MetricCollectionType.class);

    DefaultContextMetricSupport create(final InitializationContext initializationContext, final SpeciationSupport speciationSupport) {
        return DefaultContextMetricSupport.create(initializationContext, this, speciationSupport);
    }
}
