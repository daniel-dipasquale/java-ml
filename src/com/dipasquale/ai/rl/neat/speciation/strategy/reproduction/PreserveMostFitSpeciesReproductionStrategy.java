package com.dipasquale.ai.rl.neat.speciation.strategy.reproduction;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.speciation.core.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor
public final class PreserveMostFitSpeciesReproductionStrategy implements SpeciesReproductionStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = -1918824077284264052L;
    private static final boolean INCLUDE_REPRESENTATIVE_ORGANISM = false;

    @Override
    public void reproduce(final SpeciesReproductionContext context) {
        Context.SpeciationSupport speciation = context.getParent().speciation();

        for (Species species : context.getRankedSpecies()) {
            List<Organism> organisms = species.getFittestOrganisms(speciation, INCLUDE_REPRESENTATIVE_ORGANISM);

            context.getOrganismsWithoutSpecies().addAll(organisms);
        }
    }
}
