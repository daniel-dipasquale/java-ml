package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.genotype.Organism;
import com.dipasquale.ai.rl.neat.species.Species;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciesEvolutionStrategySelectMostElites implements SpeciesEvolutionStrategy {
    private final Set<Organism> organismsWithoutSpecies;

    @Override
    public void process(final SpeciesEvolutionContext context, final Species species) {
        if (!species.shouldSurvive()) {
            return;
        }

        List<Organism> eliteOrganisms = species.selectMostElites();

        int organismsPreserved = (int) eliteOrganisms.stream()
                .filter(organismsWithoutSpecies::add)
                .count();

        context.addOrganismsNeeded(-organismsPreserved);
    }

    @Override
    public void postProcess(final SpeciesEvolutionContext context) {
    }
}
