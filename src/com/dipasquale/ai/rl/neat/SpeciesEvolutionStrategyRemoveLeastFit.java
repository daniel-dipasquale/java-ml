package com.dipasquale.ai.rl.neat;

import java.util.List;

final class SpeciesEvolutionStrategyRemoveLeastFit implements SpeciesEvolutionStrategy {
    @Override
    public void process(final SpeciesEvolutionContext context, final Species species) {
        if (species.shouldSurvive()) {
            List<Organism> unfitOrganisms = species.removeUnfitToReproduce();

            context.addOrganismsNeeded(unfitOrganisms.size() + species.size() - 1);
        } else {
            context.addOrganismsNeeded(species.size());
        }
    }

    @Override
    public void postProcess(final SpeciesEvolutionContext context) {
    }
}
