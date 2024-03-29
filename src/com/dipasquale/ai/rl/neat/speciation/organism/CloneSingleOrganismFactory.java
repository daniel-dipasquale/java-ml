package com.dipasquale.ai.rl.neat.speciation.organism;

import com.dipasquale.ai.rl.neat.Context;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class CloneSingleOrganismFactory implements OrganismFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -357014493206400559L;
    private final Organism organism;

    @Override
    public Organism create(final Context context) {
        Organism copiedOrganism = organism.createCopy(context);

        copiedOrganism.registerNodeGenes(context.nodeGenes());

        return copiedOrganism;
    }
}
