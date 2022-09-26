package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class MetricsSettings {
    @Builder.Default
    private final EnumSet<MetricCollectionType> types = EnumSet.noneOf(MetricCollectionType.class);

    DefaultNeatContextMetricsSupport create(final NeatInitializationContext initializationContext, final SpeciationSettings speciationSettings) {
        return DefaultNeatContextMetricsSupport.create(initializationContext, this, speciationSettings);
    }
}
