package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.FitnessDeterminerFactory;
import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.common.SequentialIdFactoryLong;
import com.dipasquale.ai.rl.neat.context.ContextDefaultComponentFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultGeneralSupport;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefaultFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class SettingsGeneralEvaluatorSupport {
    @Builder.Default
    private final int populationSize = 150;
    private final SettingsGenomeFactory genomeFactory;
    private final Environment environment;

    ContextDefaultComponentFactory<ContextDefaultGeneralSupport> createFactory(final SettingsParallelism parallelism) { // TODO: avoid creating SequentialIdFactorySynchronized if parallism is not on
        return context -> {
            SequentialIdFactory genomeIdFactory = new SequentialIdFactorySynchronized("genome", new SequentialIdFactoryLong());
            GenomeDefaultFactory genomeFactoryFixed = genomeFactory.create(context);
            SequentialIdFactory speciesIdFactory = new SequentialIdFactorySynchronized("species", new SequentialIdFactoryLong());
            FitnessDeterminerFactory fitnessDeterminerFactory = FitnessDeterminerFactory.createLastValueFactory();

            Deque<String> discardedGenomeIds = !parallelism.isEnabled()
                    ? new LinkedList<>()
                    : new ConcurrentLinkedDeque<>();

            return new ContextDefaultGeneralSupport(populationSize, genomeIdFactory, genomeFactoryFixed, speciesIdFactory, fitnessDeterminerFactory, environment, discardedGenomeIds);
        };
    }
}
