package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.FitnessDeterminerFactory;
import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.common.SequentialIdFactoryLong;
import com.dipasquale.ai.rl.neat.context.ContextDefaultComponentFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultGeneralSupport;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefaultFactory;
import com.dipasquale.common.IdFactory;
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

    ContextDefaultComponentFactory<ContextDefaultGeneralSupport> createFactory(final SettingsParallelism parallelism) {
        return context -> {
            SequentialIdFactory genomeIdFactory = new SequentialIdFactoryDefault("genome", new SequentialIdFactoryLong());
            IdFactory<String> genomeIdFactoryFixed = () -> genomeIdFactory.next().toString();
            GenomeDefaultFactory genomeFactoryFixed = genomeFactory.create(context);
            FitnessDeterminerFactory fitnessDeterminerFactory = FitnessDeterminerFactory.createLastValueFactory();
            SequentialIdFactory speciesIdFactory = new SequentialIdFactoryDefault("species", new SequentialIdFactoryLong());
            IdFactory<String> speciesIdFactoryFixed = () -> speciesIdFactory.next().toString();

            Deque<String> discardedGenomeIds = parallelism.getNumberOfThreads() == 1
                    ? new LinkedList<>()
                    : new ConcurrentLinkedDeque<>();

            return new ContextDefaultGeneralSupport(populationSize, genomeIdFactoryFixed, genomeFactoryFixed, speciesIdFactoryFixed, fitnessDeterminerFactory, environment, discardedGenomeIds);
        };
    }
}
