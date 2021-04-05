package com.dipasquale.ai.rl.neat.species;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.Organism;
import com.dipasquale.ai.rl.neat.genotype.OrganismFactory;

import java.util.List;

public interface Species { // TODO: this interface might not be needed, merge it back to the class if that's the case
    String getId();

    Organism getRepresentative();

    List<Organism> getOrganisms();

    boolean addIfCompatible(Context.Speciation speciation, Organism organism);

    void add(Context.Speciation speciation, Organism organism);

    float getSharedFitness();

    float updateSharedFitness();

    float updateFitness(Context.GeneralSupport general);

    List<Organism> removeUnfitToReproduce(Context.Speciation speciation);

    List<OrganismFactory> getOrganismsToBirth(Context context, int count);

    OrganismFactory getOrganismToBirth(Context.Random random, Species other);

    Organism selectMostElite();

    List<Organism> selectMostElites(Context.Speciation speciation);

    boolean shouldSurvive(Context.Speciation speciation);

    List<Organism> restart(Context.Random random);
}
