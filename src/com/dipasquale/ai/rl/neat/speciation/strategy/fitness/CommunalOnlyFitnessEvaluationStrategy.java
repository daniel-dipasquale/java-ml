package com.dipasquale.ai.rl.neat.speciation.strategy.fitness;

import com.dipasquale.ai.rl.neat.NeatContext;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.speciation.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.data.structure.deque.NodeDeque;
import com.dipasquale.data.structure.deque.StandardNode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommunalOnlyFitnessEvaluationStrategy implements FitnessEvaluationStrategy {
    private static final CommunalOnlyFitnessEvaluationStrategy INSTANCE = new CommunalOnlyFitnessEvaluationStrategy();

    public static CommunalOnlyFitnessEvaluationStrategy getInstance() {
        return INSTANCE;
    }

    private static Partnership createPartnership(final FitnessEvaluationContext context) {
        NodeDeque<Species, StandardNode<Species>> speciesNodes = context.getSpeciesNodes();
        NeatContext.ActivationSupport activationSupport = context.getParent().getActivation();
        List<OrganismOrigin> organismOrigins = new ArrayList<>();
        List<GenomeActivator> genomeActivators = new ArrayList<>();

        for (StandardNode<Species> speciesNode : speciesNodes) {
            Species species = speciesNodes.getValue(speciesNode);

            for (Organism organism : species.getOrganisms()) {
                organismOrigins.add(new OrganismOrigin(organism, species));
                genomeActivators.add(organism.provideActivator(activationSupport));
            }
        }

        return new Partnership(organismOrigins, genomeActivators);
    }

    @Override
    public void evaluate(final FitnessEvaluationContext context) {
        Partnership partnership = createPartnership(context);
        NeatContext parentContext = context.getParent();
        List<Float> allFitness = parentContext.getActivation().evaluateAllFitness(parentContext, partnership.genomeActivators);

        for (int i = 0, c = partnership.genomeActivators.size(); i < c; i++) {
            OrganismOrigin organismOrigin = partnership.organismOrigins.get(i);
            float fitness = allFitness.get(i);

            organismOrigin.organism.setFitness(organismOrigin.species, parentContext, fitness);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class OrganismOrigin {
        private final Organism organism;
        private final Species species;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Partnership {
        private final List<OrganismOrigin> organismOrigins;
        private final List<GenomeActivator> genomeActivators;
    }
}
