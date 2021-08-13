/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.speciation.strategy.breeding;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.speciation.core.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.ai.rl.neat.speciation.organism.OrganismFactory;
import com.dipasquale.common.Pair;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Queue;
import java.util.Set;

@RequiredArgsConstructor
public final class InterSpeciesBreedingStrategy implements SpeciesBreedingStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = -1478796037896876768L;
    private final Context context;
    private final Set<Organism> organismsWithoutSpecies;
    private final Queue<OrganismFactory> organismsToBirth;

    @Override
    public void process(final SpeciesBreedingContext breedContext, final List<Species> speciesList) {
        int size = speciesList.size();
        float organismsNeeded = (float) (context.general().populationSize() - size - organismsWithoutSpecies.size() - organismsToBirth.size());
        float organismsToReproduce = organismsNeeded * context.speciation().interSpeciesMatingRate() + breedContext.getInterSpeciesBreedingLeftOverRatio();
        int organismsToReproduceFixed = (int) Math.floor(organismsToReproduce);

        breedContext.setInterSpeciesBreedingLeftOverRatio(organismsToReproduce - (float) organismsToReproduceFixed); // TODO: revisit this algorithm

        if (size >= 2) {
            for (int i = 0; i < organismsToReproduceFixed; i++) {
                Pair<Species> speciesPair = context.random().nextUniquePair(speciesList);
                OrganismFactory organismToBirth = speciesPair.getLeft().getOrganismToBirth(context.random(), speciesPair.getRight());

                organismsToBirth.add(organismToBirth);
            }
        }
    }
}
