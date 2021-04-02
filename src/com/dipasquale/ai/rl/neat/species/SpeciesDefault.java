package com.dipasquale.ai.rl.neat.species;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.Organism;
import com.dipasquale.ai.rl.neat.genotype.OrganismFactoryMating;
import com.dipasquale.ai.rl.neat.genotype.OrganismFactoryMutation;
import com.dipasquale.ai.rl.neat.population.Population;
import com.dipasquale.common.ObjectFactory;
import com.dipasquale.common.Pair;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SpeciesDefault implements Species {
    private final Context context;
    @Getter
    private final String id;
    private final Population population;
    private Organism representativeOrganism;
    private List<Organism> organisms;
    private List<Organism> organismsReadOnly;
    private boolean isOrganismsSorted;
    @Getter
    private float sharedFitness;
    private float maximumSharedFitness;
    private final int birthGeneration;
    private int ageLastImproved;

    public SpeciesDefault(final Context context, final Population population, final Organism representativeOrganism) {
        List<Organism> organisms = Lists.newArrayList(representativeOrganism);

        this.context = context;
        this.id = context.general().createSpeciesId();
        this.population = population;
        this.representativeOrganism = representativeOrganism;
        representativeOrganism.setMostCompatibleSpecies(this);
        this.organisms = organisms;
        this.organismsReadOnly = Collections.unmodifiableList(organisms);
        this.isOrganismsSorted = true;
        this.sharedFitness = 0f;
        this.maximumSharedFitness = 0f;
        this.birthGeneration = population.getGeneration();
        this.ageLastImproved = 0;
    }

    @Override
    public Organism getRepresentative() {
        return representativeOrganism;
    }

    @Override
    public List<Organism> getOrganisms() {
        return organismsReadOnly;
    }

    private void setOrganisms(final List<Organism> newOrganisms) {
        organisms = newOrganisms;
        organismsReadOnly = Collections.unmodifiableList(newOrganisms);
    }

    private int getAge() {
        return population.getGeneration() - birthGeneration;
    }

    @Override
    public boolean addIfCompatible(final Organism organism) {
        if (organisms.size() < context.speciation().maximumGenomes() && organism.isCompatible(this)) {
            organism.setMostCompatibleSpecies(this);
            organisms.add(organism);
            isOrganismsSorted = false;

            return true;
        }

        return false;
    }

    @Override
    public void add(final Organism organism) {
        if (organisms.size() < context.speciation().maximumGenomes()) {
            organism.setMostCompatibleSpecies(this);
            organisms.add(organism);
            isOrganismsSorted = false;
        }
    }

    private float updateFitness(final OrganismFitness organismFitness) {
        float fitnessTotal = 0f;

        for (Organism organism : organisms) {
            fitnessTotal += organismFitness.getOrUpdate(organism);
        }

        float sharedFitnessNew = fitnessTotal / organisms.size();
        int age = getAge();
        int ageLastImprovedNew = Float.compare(sharedFitnessNew, maximumSharedFitness) > 0 ? age : ageLastImproved;

        sharedFitness = sharedFitnessNew;
        maximumSharedFitness = Math.max(sharedFitnessNew, maximumSharedFitness);
        ageLastImproved = ageLastImprovedNew;

        return sharedFitness;
    }

    @Override
    public float updateSharedFitness() {
        return updateFitness(Organism::getFitness);
    }

    @Override
    public float updateFitness() {
        return updateFitness(Organism::updateFitness);
    }

    private void ensureOrganismsIsSorted() {
        if (!isOrganismsSorted) {
            isOrganismsSorted = true;
            Collections.sort(organisms);
        }
    }

    @Override
    public List<Organism> removeUnfitToReproduce() {
        int size = organisms.size();

        if (size > 1) {
            int keep = context.speciation().getFitCountToReproduce(size);
            int remove = size - keep;

            if (remove > 0) {
                ensureOrganismsIsSorted();

                List<Organism> organismsRemoved = organisms.subList(0, remove);

                setOrganisms(organisms.subList(remove, size));

                return organismsRemoved;
            }
        }

        return ImmutableList.of();
    }

    @Override
    public List<ObjectFactory<Organism>> getOrganismsToBirth(final int count) {
        List<ObjectFactory<Organism>> organismsToBirth = new ArrayList<>();
        int size = organisms.size();

        for (int i = 0; i < count; i++) {
            boolean shouldMateAndMutate = context.crossOver().shouldMateAndMutate();
            boolean shouldMate = shouldMateAndMutate || context.crossOver().shouldMateOnly();
            boolean shouldMutate = shouldMateAndMutate || !shouldMate && context.crossOver().shouldMutateOnly();

            if (size > 1 && shouldMate) {
                Pair<Organism> organismPair = context.random().nextUniquePair(organisms);

                organismsToBirth.add(new OrganismFactoryMating(organismPair.getItem1(), organismPair.getItem2(), shouldMutate));
            } else {
                Organism organism = context.random().nextItem(organisms);

                organismsToBirth.add(new OrganismFactoryMutation(organism));
            }
        }

        return organismsToBirth;
    }

    @Override
    public ObjectFactory<Organism> getOrganismToBirth(final Species other) {
        if (organisms.size() == 0 || other.getOrganisms().size() == 0) {
            return null;
        }

        Organism organism1 = context.random().nextItem(organisms);
        Organism organism2 = context.random().nextItem(other.getOrganisms());

        return new OrganismFactoryMating(organism1, organism2, false);
    }

    @Override
    public Organism selectMostElite() {
        ensureOrganismsIsSorted();

        return organisms.get(organisms.size() - 1);
    }

    @Override
    public List<Organism> selectMostElites() {
        int size = organisms.size();
        int select = context.speciation().getEliteCountToPreserve(size);

        if (select == 0) {
            return ImmutableList.of();
        }

        ensureOrganismsIsSorted();

        return organisms.subList(size - select, size);
    }

    @Override
    public boolean shouldSurvive() {
        return getAge() - ageLastImproved < context.speciation().stagnationDropOffAge();
    }

    @Override
    public List<Organism> restart() {
        int index = context.random().nextIndex(organisms.size());
        Organism representativeOrganismNew = organisms.get(index);
        List<Organism> organismsOld = organisms;

        organisms.remove(index);
        representativeOrganism = representativeOrganismNew;
        setOrganisms(Lists.newArrayList(representativeOrganismNew));
        isOrganismsSorted = true;
        sharedFitness = 0f;

        return organismsOld;
    }

    @FunctionalInterface
    private interface OrganismFitness {
        float getOrUpdate(Organism organism);
    }
}
