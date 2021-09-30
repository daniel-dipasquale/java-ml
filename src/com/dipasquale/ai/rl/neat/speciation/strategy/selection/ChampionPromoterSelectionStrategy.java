package com.dipasquale.ai.rl.neat.speciation.strategy.selection;

import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.speciation.core.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class ChampionPromoterSelectionStrategy implements SpeciesSelectionStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = 6142238994253254883L;

    @Override
    public void prepareSurvival(final SpeciesSelectionContext context, final Species species) {
        Organism championOrganism = species.getChampionOrganism();

        if (context.getChampionOrganism() == null || context.getChampionOrganism().compareTo(championOrganism) < 0) {
            context.setChampionOrganism(championOrganism);
        }
    }

    @Override
    public void prepareExtinction(final SpeciesSelectionContext context, final Species species) {
    }

    @Override
    public void finalizeSelection(final SpeciesSelectionContext context) {
        Organism championOrganism = context.getChampionOrganism();

        if (championOrganism == null) {
            throw new ChampionOrganismMissingException("the champion organism is missing");
        }

        Organism organism = championOrganism.createClone(context.getParent().connections());
        GenomeActivator genomeActivator = championOrganism.getGenomeActivator(context.getParent().activation());

        context.getChampionOrganismActivator().initialize(organism, genomeActivator);
    }
}