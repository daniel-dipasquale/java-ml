package com.dipasquale.ai.rl.neat.speciation.organism;

import com.dipasquale.ai.rl.neat.NeatContext;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class MutateSingleOrganismFactory implements OrganismFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -8710603695032354428L;
    private final Organism organism;

    @Override
    public Organism create(final NeatContext context) {
        Organism mutatedOrganism = organism.createCopy(context);

        mutatedOrganism.mutate(context);
        mutatedOrganism.registerNodeGenes(context.getNodeGenes());

        return mutatedOrganism;
    }
}
