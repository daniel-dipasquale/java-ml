package com.dipasquale.ai.rl.neat.speciation.strategy.reproduction;

import com.dipasquale.ai.rl.neat.NeatContext;
import com.dipasquale.ai.rl.neat.speciation.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.data.structure.set.DequeSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PreserveMostFitReproductionStrategy implements ReproductionStrategy {
    private static final boolean INCLUDE_REPRESENTATIVE_ORGANISM = false;
    private static final PreserveMostFitReproductionStrategy INSTANCE = new PreserveMostFitReproductionStrategy();

    public static PreserveMostFitReproductionStrategy getInstance() {
        return INSTANCE;
    }

    @Override
    public void reproduce(final ReproductionContext context) {
        NeatContext.SpeciationSupport speciationSupport = context.getParent().getSpeciation();
        DequeSet<Organism> undeterminedOrganisms = context.getUndeterminedOrganisms();

        for (Species species : context.getSpeciesState().getAll()) {
            List<Organism> organisms = species.getFittestOrganisms(speciationSupport, INCLUDE_REPRESENTATIVE_ORGANISM);

            undeterminedOrganisms.addAll(organisms);
        }
    }
}
