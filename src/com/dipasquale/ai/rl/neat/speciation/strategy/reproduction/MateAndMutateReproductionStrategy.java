package com.dipasquale.ai.rl.neat.speciation.strategy.reproduction;

import com.dipasquale.ai.rl.neat.NeatContext;
import com.dipasquale.ai.rl.neat.speciation.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.ai.rl.neat.speciation.organism.OrganismFactory;
import com.dipasquale.common.Pair;
import com.dipasquale.data.structure.set.DequeSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Queue;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MateAndMutateReproductionStrategy implements ReproductionStrategy {
    private static final MateAndMutateReproductionStrategy INSTANCE = new MateAndMutateReproductionStrategy();

    public static MateAndMutateReproductionStrategy getInstance() {
        return INSTANCE;
    }

    private void reproduceInterSpecies(final NeatContext.RandomnessSupport randomnessSupport, final List<Species> rankedSpecies, final Queue<OrganismFactory> organismsToBirth) {
        Pair<Species> speciesPair = randomnessSupport.generateElementPair(rankedSpecies);
        OrganismFactory organismToBirth = speciesPair.getLeft().reproduce(randomnessSupport, speciesPair.getRight());

        organismsToBirth.add(organismToBirth);
    }

    private int reproduceInterSpecies(final ReproductionContext context) {
        NeatContext parentContext = context.getParent();
        int populationSize = parentContext.getSpeciation().getPopulationSize();
        SpeciesState speciesState = context.getSpeciesState();
        List<Species> rankedSpecies = speciesState.getRanked();
        int reproduced = 0;

        if (rankedSpecies.size() >= 2) {
            int speciesCount = speciesState.getAll().size();
            DequeSet<Organism> undeterminedOrganisms = context.getUndeterminedOrganisms();
            Queue<OrganismFactory> organismsToBirth = context.getOrganismsToBirth();
            float organismsNeeded = (float) (populationSize - speciesCount - undeterminedOrganisms.size() - organismsToBirth.size());
            float organismsToReproduce = organismsNeeded * parentContext.getSpeciation().getInterSpeciesMatingRate();
            float flooredOrganismsToReproduce = (float) Math.floor(organismsToReproduce);
            int fixedFlooredOrganismsToReproduce = (int) flooredOrganismsToReproduce;
            NeatContext.RandomnessSupport randomnessSupport = parentContext.getRandomness();

            for (int i = 0; i < fixedFlooredOrganismsToReproduce; i++) {
                reproduceInterSpecies(randomnessSupport, rankedSpecies, organismsToBirth);
            }

            reproduced = fixedFlooredOrganismsToReproduce;

            if (randomnessSupport.isLessThan(organismsToReproduce - flooredOrganismsToReproduce)) {
                reproduceInterSpecies(randomnessSupport, rankedSpecies, organismsToBirth);
                reproduced++;
            }
        }

        return reproduced;
    }

    private void reproduceIntraSpecies(final NeatContext context, final Queue<OrganismFactory> organismsToBirth, final Species species, final int reproductionCount) {
        organismsToBirth.addAll(species.reproduce(context, reproductionCount));
    }

    private void reproduceIntraSpecies(final ReproductionContext context) {
        NeatContext parentContext = context.getParent();
        int populationSize = parentContext.getSpeciation().getPopulationSize();
        SpeciesState speciesState = context.getSpeciesState();
        List<Species> rankedSpecies = speciesState.getRanked();
        int speciesCount = speciesState.getAll().size();
        DequeSet<Organism> undeterminedOrganisms = context.getUndeterminedOrganisms();
        Queue<OrganismFactory> organismsToBirth = context.getOrganismsToBirth();
        float organismsNeeded = (float) (populationSize - speciesCount - undeterminedOrganisms.size() - organismsToBirth.size());
        float organismsReproducedPrevious;
        float organismsReproduced = 0f;

        for (Species species : rankedSpecies) {
            float reproductionFloat = organismsNeeded * species.getSharedFitness() / speciesState.getTotalFitness();

            organismsReproducedPrevious = organismsReproduced;
            organismsReproduced += reproductionFloat;

            int reproductionCount = (int) organismsReproduced - (int) organismsReproducedPrevious;

            reproduceIntraSpecies(parentContext, organismsToBirth, species, reproductionCount);
        }

        int organismsNeededStill = populationSize - speciesCount - undeterminedOrganisms.size() - organismsToBirth.size();

        if (organismsNeededStill > 0) { // NOTE: floating point problem
            Species species = rankedSpecies.get(rankedSpecies.size() - 1);

            reproduceIntraSpecies(parentContext, organismsToBirth, species, organismsNeededStill);
        }
    }

    @Override
    public void reproduce(final ReproductionContext context) {
        reproduceInterSpecies(context);
        reproduceIntraSpecies(context);
    }
}
