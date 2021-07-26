package com.dipasquale.ai.rl.neat.speciation;

import com.dipasquale.ai.rl.neat.context.Context;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciesEvolutionStrategySelectMostElites implements SpeciesEvolutionStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = -1918824077284264052L;
    private final Context.SpeciationSupport speciation;
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
