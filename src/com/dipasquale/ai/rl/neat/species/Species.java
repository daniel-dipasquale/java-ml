package com.dipasquale.ai.rl.neat.species;

import com.dipasquale.ai.rl.neat.genotype.Organism;

import java.util.List;

public interface Species {
    String getId();

    Organism getRepresentative();

    List<Organism> getOrganisms();

    boolean addIfCompatible(Organism organism);

    void add(Organism organism);

    float getSharedFitness();

    float updateSharedFitness();

    float updateFitness();

    List<Organism> removeUnfitToReproduce();

    List<Organism> reproduceOutcast(int count);

    Organism reproduceOutcast(Species other);

    Organism selectMostElite();

    List<Organism> selectMostElites();

    boolean shouldSurvive();

    List<Organism> restart();
}
