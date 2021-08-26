package com.dipasquale.ai.rl.neat.speciation.strategy.reproduction;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.speciation.core.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor
public final class GenesisSpeciesReproductionStrategy implements SpeciesReproductionStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = -4687936745852249338L;

    @Override
    public void reproduce(final SpeciesReproductionContext context) {
        Context.RandomSupport random = context.getParent().random();
        Context.SpeciationSupport speciation = context.getParent().speciation();

        for (Species species : context.getRankedSpecies()) {
            List<Organism> organismsKilled = species.restart(random, context.getOrganismsWithoutSpecies());

            organismsKilled.forEach(ok -> ok.kill(speciation));
        }
    }
}
