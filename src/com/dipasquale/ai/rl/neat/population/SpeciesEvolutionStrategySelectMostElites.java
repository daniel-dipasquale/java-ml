package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.Organism;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciesEvolutionStrategySelectMostElites implements SpeciesEvolutionStrategy {
    private final Context.Speciation speciation;
    private final Set<Organism> organismsWithoutSpecies;

    @Override
    public void process(final SpeciesEvolutionContext evolutionContext, final Species species, boolean speciesSurvives) {
        if (!speciesSurvives) {
            return;
        }

        List<Organism> eliteOrganisms = species.selectMostElites(speciation);

        organismsWithoutSpecies.addAll(eliteOrganisms);
    }

    @Override
    public void postProcess(final SpeciesEvolutionContext evolutionContext) {
    }
}
