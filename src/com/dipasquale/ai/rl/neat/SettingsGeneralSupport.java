package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.IdFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class SettingsGeneralSupport<T extends Comparable<T>> {
    @Builder.Default
    private final int populationSize = 100;
    @Builder.Default
    private final IdFactory<String> genomeIdFactory = () -> UUID.randomUUID().toString();
    private final SettingsGenomeFactory genomeFactory;
    @Builder.Default
    private final IdFactory<String> speciesIdFactory = () -> UUID.randomUUID().toString();
    @Builder.Default
    private final FitnessDeterminerFactory fitnessDeterminerFactory = FitnessDeterminerFactory.createLastValueFactory();
    private final Environment environment;

    ContextDefaultComponentFactory<T, ContextDefaultGeneralSupport<T>> createFactory() {
        return c -> new ContextDefaultGeneralSupport<>(populationSize, genomeIdFactory, genomeFactory.create(c), speciesIdFactory, fitnessDeterminerFactory, environment);
    }
}
