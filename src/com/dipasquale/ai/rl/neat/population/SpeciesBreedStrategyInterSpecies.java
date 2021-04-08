package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.Organism;
import com.dipasquale.ai.rl.neat.genotype.OrganismFactory;
import com.dipasquale.ai.rl.neat.genotype.Species;
import com.dipasquale.common.Pair;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.List;
import java.util.Queue;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciesBreedStrategyInterSpecies implements SpeciesBreedStrategy {
    @Serial
    private static final long serialVersionUID = 6826862685517027232L;
    private final Context context;
    private final Set<Organism> organismsWithoutSpecies;
    private final Queue<OrganismFactory> organismsToBirth;

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
                OrganismFactory organismToBirth = speciesPair.getItem1().getOrganismToBirth(context.random(), speciesPair.getItem2());

                organismsToBirth.add(organismToBirth);
            }
        }
    }
}
