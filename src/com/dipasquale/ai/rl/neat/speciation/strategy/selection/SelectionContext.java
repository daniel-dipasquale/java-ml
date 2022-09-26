package com.dipasquale.ai.rl.neat.speciation.strategy.selection;

import com.dipasquale.ai.rl.neat.NeatContext;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.speciation.Population;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public final class SelectionContext {
    private final NeatContext parent;
    private final Population population;
    @Setter
    private Organism championOrganism = null;

    public void initializeChampionOrganism() {
        if (championOrganism == null) {
            throw new ChampionOrganismMissingException("the champion organism is missing");
        }

        Organism organism = championOrganism.createClone(parent.getConnectionGenes());
        GenomeActivator genomeActivator = championOrganism.createTransientActivator(parent.getActivation());

        population.initializeChampionOrganism(organism, genomeActivator);
    }
}
