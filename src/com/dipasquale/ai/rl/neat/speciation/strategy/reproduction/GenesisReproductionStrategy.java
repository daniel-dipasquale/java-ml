package com.dipasquale.ai.rl.neat.speciation.strategy.reproduction;

import com.dipasquale.ai.rl.neat.Context;
import com.dipasquale.ai.rl.neat.speciation.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor
public final class GenesisReproductionStrategy implements ReproductionStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = -4687936745852249338L;

    private static void killOrganism(final Organism organism, final Context context) {
        organism.kill(context.speciation());
        organism.deregisterNodeGenes(context.nodeGenes());
    }

    @Override
    public void reproduce(final ReproductionContext context) {
        Context.RandomnessSupport randomnessSupport = context.getParent().randomness();
        Context.MetricsSupport metricsSupport = context.getParent().metrics();

        for (Species species : context.getSpeciesState().getAll()) {
            List<Organism> organismsToKill = species.restart(randomnessSupport, context.getOrganismsWithoutSpecies());

            organismsToKill.forEach(organismToKill -> killOrganism(organismToKill, context.getParent()));
            metricsSupport.collectKilled(species, organismsToKill);
        }
    }
}
