package com.dipasquale.ai.rl.neat.speciation.strategy.fitness;

import com.dipasquale.ai.rl.neat.Context;
import com.dipasquale.ai.rl.neat.speciation.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class ConcurrentOrganismFitnessCalculationStrategy implements FitnessCalculationStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = 5632936446515400703L;

    private static void updateFitness(final OrganismHierarchy organismHierarchy, final Context context) {
        organismHierarchy.organism.updateFitness(organismHierarchy.species, context);
    }

    @Override
    public void calculate(final FitnessCalculationContext context) {
        List<OrganismHierarchy> organismHierarchies = context.getSpeciesNodes().flattenedStream()
                .flatMap(species -> species.getOrganisms().stream()
                        .map(organism -> new OrganismHierarchy(organism, species)))
                .collect(Collectors.toList());

        context.getParent().parallelism().forEach(organismHierarchies, organismHierarchy -> updateFitness(organismHierarchy, context.getParent()));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class OrganismHierarchy {
        private final Organism organism;
        private final Species species;
    }
}
