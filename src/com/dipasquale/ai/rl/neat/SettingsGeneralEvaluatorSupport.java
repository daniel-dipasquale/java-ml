package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.FitnessDeterminerFactory;
import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.common.SequentialIdFactoryDefault;
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
    private final FitnessDeterminerFactory fitnessDeterminerFactory;
    private final NeatEnvironment environment;

    ContextDefaultGeneralSupport create(final SettingsConnectionGeneSupport connections, final SettingsParallelism parallelism) {
        ArgumentValidatorUtils.ensureGreaterThanOrEqualTo(populationSize, 20, "populationSize");

        SequentialIdFactory genomeIdFactory = parallelism.createSequentialIdFactory("genome", new SequentialIdFactoryDefault());
        GenomeDefaultFactory genomeFactoryFixed = genomeFactory.create(connections, parallelism);
        SequentialIdFactory speciesIdFactory = parallelism.createSequentialIdFactory("species", new SequentialIdFactoryDefault());

        Deque<String> discardedGenomeIds = !parallelism.isEnabled()
                ? new LinkedList<>()
                : new ConcurrentLinkedDeque<>();

        return new ContextDefaultGeneralSupport(populationSize, genomeIdFactory, genomeFactoryFixed, speciesIdFactory, fitnessDeterminerFactory, environment, discardedGenomeIds);
    }
}
