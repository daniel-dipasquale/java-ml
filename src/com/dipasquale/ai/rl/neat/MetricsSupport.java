package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class MetricsSupport {
    @Builder.Default
    private final EnumSet<MetricCollectionType> types = EnumSet.noneOf(MetricCollectionType.class);

    ContextObjectMetricsSupport create(final InitializationContext initializationContext, final SpeciationSupport speciationSupport) {
        return ContextObjectMetricsSupport.create(initializationContext, this, speciationSupport);
    }
}
