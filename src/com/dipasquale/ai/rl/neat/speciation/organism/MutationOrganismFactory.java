/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.speciation.organism;

import com.dipasquale.ai.rl.neat.context.Context;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class MutationOrganismFactory implements OrganismFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -8710603695032354428L;
    private final Organism originalOrganism;

    @Override
    public Organism create(final Context context) {
        Organism organism = originalOrganism.createCopy();

        organism.prepare(context);
        organism.mutate(context);
        organism.freeze();

        return organism;
    }
}
