package com.dipasquale.ai.rl.neat.speciation;

import com.dipasquale.ai.rl.neat.context.Context;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciesEvolutionStrategySelectMostElites implements SpeciesEvolutionStrategy {
    @Serial
    private static final long serialVersionUID = 5390002373843857340L;
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
