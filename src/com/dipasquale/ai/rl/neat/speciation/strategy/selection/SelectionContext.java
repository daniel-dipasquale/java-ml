package com.dipasquale.ai.rl.neat.speciation.strategy.selection;

import com.dipasquale.ai.rl.neat.Context;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.speciation.Population;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public final class SelectionContext {
    private final Context parent;
    private final Population population;
    @Setter
    private Organism championOrganism = null;

    public void setChampionOrganism() {
        if (championOrganism == null) {
            throw new ChampionOrganismMissingException("the champion organism is missing");
        }

        Organism organism = championOrganism.createClone(parent.connectionGenes());
        GenomeActivator genomeActivator = championOrganism.createTransientActivator(parent.activation());

        population.initializeChampionOrganism(organism, genomeActivator);
    }
}
