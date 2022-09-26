package com.dipasquale.ai.rl.neat.speciation.strategy.reproduction;

import com.dipasquale.ai.rl.neat.NeatContext;
import com.dipasquale.ai.rl.neat.speciation.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.data.structure.set.DequeSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenesisReproductionStrategy implements ReproductionStrategy {
    private static final GenesisReproductionStrategy INSTANCE = new GenesisReproductionStrategy();

    public static GenesisReproductionStrategy getInstance() {
        return INSTANCE;
    }

    private static void killOrganism(final Organism organism, final NeatContext context) {
        organism.kill(context.getSpeciation());
        organism.deregisterNodeGenes(context.getNodeGenes());
    }

    @Override
    public void reproduce(final ReproductionContext context) {
        NeatContext parentContext = context.getParent();
        NeatContext.RandomnessSupport randomnessSupport = parentContext.getRandomness();
        DequeSet<Organism> undeterminedOrganisms = context.getUndeterminedOrganisms();
        NeatContext.MetricsSupport metricsSupport = parentContext.getMetrics();

        for (Species species : context.getSpeciesState().getAll()) {
            List<Organism> organismsToKill = species.restart(randomnessSupport, undeterminedOrganisms);

            organismsToKill.forEach(organismToKill -> killOrganism(organismToKill, parentContext));
            metricsSupport.collectKilled(species, organismsToKill);
        }
    }
}
