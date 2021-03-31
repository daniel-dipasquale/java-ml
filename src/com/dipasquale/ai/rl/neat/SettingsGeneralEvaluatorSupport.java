package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.FitnessDeterminerFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultComponentFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultGeneralSupport;
import com.dipasquale.common.IdFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class SettingsGeneralEvaluatorSupport {
    @Builder.Default
    private final int populationSize = 150;
    @Builder.Default
    private final IdFactory<String> genomeIdFactory = () -> UUID.randomUUID().toString(); // TODO: consider reusing IDS, since population sizes are fixed, reusable ids are more useful
    private final SettingsGenomeFactory genomeFactory;
    @Builder.Default
    private final IdFactory<String> speciesIdFactory = () -> UUID.randomUUID().toString();
    private final Environment environment;

    ContextDefaultComponentFactory<ContextDefaultGeneralSupport> createFactory() {
        FitnessDeterminerFactory fitnessDeterminerFactory = FitnessDeterminerFactory.createLastValueFactory();

        return context -> new ContextDefaultGeneralSupport(populationSize, genomeIdFactory, genomeFactory.create(context), speciesIdFactory, fitnessDeterminerFactory, environment);
    }
}
