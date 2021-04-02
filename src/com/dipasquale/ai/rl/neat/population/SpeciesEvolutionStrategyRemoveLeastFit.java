package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.genotype.Organism;
import com.dipasquale.ai.rl.neat.species.Species;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciesEvolutionStrategyRemoveLeastFit implements SpeciesEvolutionStrategy {
    @Override
    public void process(final SpeciesEvolutionContext evolutionContext, final Species species, boolean speciesSurvives) {
        if (!speciesSurvives) {
            species.getOrganisms().forEach(Organism::kill);
        } else {
            species.removeUnfitToReproduce().forEach(Organism::kill);
        }
    }

    @Override
    public void postProcess(final SpeciesEvolutionContext evolutionContext) {
    }
}
