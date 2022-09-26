package com.dipasquale.ai.rl.neat.speciation.strategy.fitness;

import com.dipasquale.ai.rl.neat.NeatContext;
import com.dipasquale.ai.rl.neat.speciation.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConcurrentOrganismFitnessEvaluationStrategy implements FitnessEvaluationStrategy {
    private static final ConcurrentOrganismFitnessEvaluationStrategy INSTANCE = new ConcurrentOrganismFitnessEvaluationStrategy();

    public static ConcurrentOrganismFitnessEvaluationStrategy getInstance() {
        return INSTANCE;
    }

    private static void updateFitness(final OrganismOrigin organismOrigin, final NeatContext context) {
        organismOrigin.organism.updateFitness(organismOrigin.species, context);
    }

    @Override
    public void evaluate(final FitnessEvaluationContext context) {
        List<OrganismOrigin> organismOrigins = context.getSpeciesNodes().flattenedStream()
                .flatMap(species -> species.getOrganisms().stream()
                        .map(organism -> new OrganismOrigin(organism, species)))
                .collect(Collectors.toList());

        context.getParent().getParallelism().forEach(organismOrigins, organismOrigin -> updateFitness(organismOrigin, context.getParent()));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class OrganismOrigin {
        private final Organism organism;
        private final Species species;
    }
}
