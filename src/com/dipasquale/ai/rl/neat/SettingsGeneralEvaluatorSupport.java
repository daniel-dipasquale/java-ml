package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.FitnessDeterminerFactory;
import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.common.SequentialIdFactoryLong;
import com.dipasquale.ai.rl.neat.context.ContextDefaultComponentFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultGeneralSupport;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefaultFactory;
import com.dipasquale.common.ArgumentValidatorUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter(AccessLevel.PACKAGE)
public final class SettingsGeneralEvaluatorSupport {
    private final int populationSize;
    private final SettingsGenomeFactory genomeFactory;
    private final Environment environment;

    ContextDefaultComponentFactory<ContextDefaultGeneralSupport> createFactory(final SettingsConnectionGeneSupport connections, final SettingsParallelism parallelism) { // TODO: avoid creating SequentialIdFactorySynchronized if parallism is not on
        return context -> {
            ArgumentValidatorUtils.ensureGreaterThanOrEqualTo(populationSize, 20, "populationSize");

            SequentialIdFactory genomeIdFactory = parallelism.createSequentialIdFactory("genome", new SequentialIdFactoryLong());
            GenomeDefaultFactory genomeFactoryFixed = genomeFactory.create(context, connections, parallelism);
            SequentialIdFactory speciesIdFactory = parallelism.createSequentialIdFactory("species", new SequentialIdFactoryLong());
            FitnessDeterminerFactory fitnessDeterminerFactory = FitnessDeterminerFactory.createLastValueFactory();

            Deque<String> discardedGenomeIds = !parallelism.isEnabled()
                    ? new LinkedList<>()
                    : new ConcurrentLinkedDeque<>();

            return new ContextDefaultGeneralSupport(populationSize, genomeIdFactory, genomeFactoryFixed, speciesIdFactory, fitnessDeterminerFactory, environment, discardedGenomeIds);
        };
    }
}
