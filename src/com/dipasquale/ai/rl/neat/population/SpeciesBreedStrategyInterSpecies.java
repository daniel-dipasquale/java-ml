package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.Organism;
import com.dipasquale.ai.rl.neat.species.Species;
import com.dipasquale.common.ObjectFactory;
import com.dipasquale.common.Pair;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Queue;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciesBreedStrategyInterSpecies implements SpeciesBreedStrategy {
    private final Context context;
    private final Set<Organism> organismsWithoutSpecies;
    private final Queue<ObjectFactory<Organism>> organismsToBirth;

    @Override
    public void process(final SpeciesBreedContext breedContext, final List<Species> speciesList) {
        int size = speciesList.size();
        float organismsNeeded = (float) (context.general().populationSize() - size - organismsWithoutSpecies.size() - organismsToBirth.size());
        float organismsToReproduce = organismsNeeded * context.speciation().interSpeciesMatingRate() + breedContext.getInterSpeciesBreedingLeftOverRatio();
        int organismsToReproduceFixed = (int) Math.floor(organismsToReproduce);

        breedContext.setInterSpeciesBreedingLeftOverRatio(organismsToReproduce - (float) organismsToReproduceFixed); // TODO: revisit this algorithm

        if (size >= 2) {
            for (int i = 0; i < organismsToReproduceFixed; i++) {
                Pair<Species> speciesPair = context.random().nextUniquePair(speciesList);
                ObjectFactory<Organism> organismToBirth = speciesPair.getItem1().getOrganismToBirth(speciesPair.getItem2());

                organismsToBirth.add(organismToBirth);
            }
        }
    }
}
