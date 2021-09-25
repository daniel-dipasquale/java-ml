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

    private static void killOrganism(final Organism organism, final Context context) {
        organism.kill(context.speciation());
        organism.deregisterNodes(context.connections());
    }

    @Override
    public void reproduce(final SpeciesReproductionContext context) {
        Context.RandomSupport randomSupport = context.getParent().random();

        for (Species species : context.getSpeciesState().getAll()) {
            List<Organism> organismsToKill = species.restart(randomSupport, context.getOrganismsWithoutSpecies());

            organismsToKill.forEach(otk -> killOrganism(otk, context.getParent()));
        }
    }
}
