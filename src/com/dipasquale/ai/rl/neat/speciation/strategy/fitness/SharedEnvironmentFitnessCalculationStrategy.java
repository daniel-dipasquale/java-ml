package com.dipasquale.ai.rl.neat.speciation.strategy.fitness;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.speciation.core.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.data.structure.deque.SimpleNode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class SharedEnvironmentFitnessCalculationStrategy implements FitnessCalculationStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = -2984214590812474966L;

    private OrganismGenomeZippedCollections createOrganismGenomeZippedCollections(final FitnessCalculationContext context) {
        List<OrganismHierarchy> organismHierarchies = new ArrayList<>();
        List<GenomeActivator> genomeActivators = new ArrayList<>();
        Context.ActivationSupport activationSupport = context.getParent().activation();

        for (SimpleNode<Species> speciesNode : context.getSpeciesNodes()) {
            Species species = context.getSpeciesNodes().getValue(speciesNode);

            for (Organism organism : species.getOrganisms()) {
                organismHierarchies.add(new OrganismHierarchy(organism, species));
                genomeActivators.add(organism.getActivator(activationSupport));
            }
        }

        return new OrganismGenomeZippedCollections(organismHierarchies, genomeActivators);
    }

    @Override
    public void calculate(final FitnessCalculationContext context) {
        OrganismGenomeZippedCollections zippedCollections = createOrganismGenomeZippedCollections(context);
        List<Float> allFitness = context.getParent().activation().calculateAllFitness(context.getParent(), zippedCollections.genomeActivators);

        for (int i = 0, c = zippedCollections.genomeActivators.size(); i < c; i++) {
            OrganismHierarchy organismHierarchy = zippedCollections.organismHierarchies.get(i);
            float fitness = allFitness.get(i);

            organismHierarchy.organism.setFitness(organismHierarchy.species, context.getParent(), fitness);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class OrganismHierarchy {
        private final Organism organism;
        private final Species species;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class OrganismGenomeZippedCollections {
        private final List<OrganismHierarchy> organismHierarchies;
        private final List<GenomeActivator> genomeActivators;
    }
}
