package com.dipasquale.ai.rl.neat.speciation.strategy.reproduction;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.speciation.core.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.ai.rl.neat.speciation.organism.OrganismFactory;
import com.dipasquale.common.Pair;
import com.dipasquale.data.structure.set.DequeSet;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Queue;

@RequiredArgsConstructor
public final class MateAndMutateSpeciesReproductionStrategy implements SpeciesReproductionStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = -8229774363987716310L;

    private void reproduceInterSpecies(final Context.RandomSupport randomSupport, final List<Species> rankedSpecies, final Queue<OrganismFactory> organismsToBirth) {
        Pair<Species> speciesPair = randomSupport.generateItemPair(rankedSpecies);
        OrganismFactory organismToBirth = speciesPair.getLeft().reproduce(randomSupport, speciesPair.getRight());

        organismsToBirth.add(organismToBirth);
    }

    private void reproduceInterSpecies(final SpeciesReproductionContext context) {
        int populationSize = context.getParent().general().params().populationSize();
        List<Species> rankedSpecies = context.getSpeciesState().getRanked();

        if (rankedSpecies.size() >= 2) {
            int speciesCount = context.getSpeciesState().getAll().size();
            DequeSet<Organism> organismsWithoutSpecies = context.getOrganismsWithoutSpecies();
            Queue<OrganismFactory> organismsToBirth = context.getOrganismsToBirth();
            float organismsNeeded = (float) (populationSize - speciesCount - organismsWithoutSpecies.size() - organismsToBirth.size());
            float organismsToReproduce = organismsNeeded * context.getParent().speciation().params().interSpeciesMatingRate();
            float organismsToReproduceFloored = (float) Math.floor(organismsToReproduce);

            for (int i = 0, c = (int) organismsToReproduceFloored; i < c; i++) {
                reproduceInterSpecies(context.getParent().random(), rankedSpecies, organismsToBirth);
            }

            if (context.getParent().random().isLessThan(organismsToReproduce - organismsToReproduceFloored)) {
                reproduceInterSpecies(context.getParent().random(), rankedSpecies, organismsToBirth);
            }
        }
    }

    private void reproduceIntraSpecies(final Context context, final Queue<OrganismFactory> organismsToBirth, final Species species, final int reproductionCount) {
        organismsToBirth.addAll(species.reproduce(context, reproductionCount));
    }

    private void reproduceIntraSpecies(final SpeciesReproductionContext context) {
        int populationSize = context.getParent().general().params().populationSize();
        List<Species> rankedSpecies = context.getSpeciesState().getRanked();
        int speciesCount = context.getSpeciesState().getAll().size();
        DequeSet<Organism> organismsWithoutSpecies = context.getOrganismsWithoutSpecies();
        Queue<OrganismFactory> organismsToBirth = context.getOrganismsToBirth();
        float organismsNeeded = (float) (populationSize - speciesCount - organismsWithoutSpecies.size() - organismsToBirth.size());
        float organismsReproducedPrevious;
        float organismsReproduced = 0f;

        for (Species species : rankedSpecies) {
            float reproductionFloat = organismsNeeded * species.getSharedFitness() / context.getSpeciesState().getTotalSharedFitness();

            organismsReproducedPrevious = organismsReproduced;
            organismsReproduced += reproductionFloat;

            int reproductionCount = (int) organismsReproduced - (int) organismsReproducedPrevious;

            reproduceIntraSpecies(context.getParent(), organismsToBirth, species, reproductionCount);
        }

        int organismsNeededStill = populationSize - speciesCount - organismsWithoutSpecies.size() - organismsToBirth.size();

        if (organismsNeededStill > 0) { // NOTE: floating point problem
            Species species = rankedSpecies.get(rankedSpecies.size() - 1);

            reproduceIntraSpecies(context.getParent(), organismsToBirth, species, organismsNeededStill);
        }
    }

    @Override
    public void reproduce(final SpeciesReproductionContext context) {
        reproduceInterSpecies(context);
        reproduceIntraSpecies(context);
    }
}
