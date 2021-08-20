package com.dipasquale.ai.rl.neat.speciation.core;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.speciation.organism.CloneSingleOrganismFactory;
import com.dipasquale.ai.rl.neat.speciation.organism.MateBetweenOrganismsFactory;
import com.dipasquale.ai.rl.neat.speciation.organism.MutateSingleOrganismFactory;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.ai.rl.neat.speciation.organism.OrganismFactory;
import com.dipasquale.common.Pair;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class Species implements Serializable {
    @Serial
    private static final long serialVersionUID = 3072560376586619614L;
    @Getter
    @EqualsAndHashCode.Include
    private final String id;
    @EqualsAndHashCode.Include
    private Organism representativeOrganism;
    @EqualsAndHashCode.Include
    private List<Organism> organisms;
    private List<Organism> organismsReadOnly;
    private boolean isOrganismsSorted;
    private final PopulationState population;
    @Getter
    @EqualsAndHashCode.Include
    private float sharedFitness;
    private float maximumSharedFitness;
    private final int createdOnGeneration;
    private int ageLastImproved;

    public Species(final Organism representativeOrganism, final PopulationState population) {
        List<Organism> organisms = Lists.newArrayList(representativeOrganism);

        this.id = population.getHistoricalMarkings().createSpecies();
        this.representativeOrganism = representativeOrganism;
        representativeOrganism.setMostCompatibleSpecies(this);
        this.organisms = organisms;
        this.organismsReadOnly = Collections.unmodifiableList(organisms);
        this.isOrganismsSorted = true;
        this.population = population;
        this.sharedFitness = 0f;
        this.maximumSharedFitness = 0f;
        this.createdOnGeneration = population.getGeneration();
        this.ageLastImproved = 0;
    }

    public Organism getRepresentative() {
        return representativeOrganism;
    }

    public List<Organism> getOrganisms() {
        return organismsReadOnly;
    }

    private void setOrganisms(final List<Organism> newOrganisms) {
        organisms = newOrganisms;
        organismsReadOnly = Collections.unmodifiableList(newOrganisms);
    }

    private int getAge() {
        return population.getGeneration() - createdOnGeneration;
    }

    public boolean addIfCompatible(final Context.SpeciationSupport speciation, final Organism organism) {
        if (organisms.size() < speciation.params().maximumGenomes() && organism.isCompatible(speciation, this)) {
            organism.setMostCompatibleSpecies(this);
            organisms.add(organism);
            isOrganismsSorted = false;

            return true;
        }

        return false;
    }

    public void add(final Context.SpeciationSupport speciation, final Organism organism) {
        if (organisms.size() < speciation.params().maximumGenomes()) {
            organism.setMostCompatibleSpecies(this);
            organisms.add(organism);
            isOrganismsSorted = false;
        }
    }

    private float updateFitness(final OrganismFitness organismFitness) {
        float fitnessTotal = organisms.stream()
                .map(organismFitness::getOrUpdate)
                .reduce(0f, Float::sum);

        float sharedFitnessNew = fitnessTotal / organisms.size();

        sharedFitness = sharedFitnessNew;

        if (Float.compare(sharedFitnessNew, maximumSharedFitness) > 0) {
            maximumSharedFitness = sharedFitnessNew;
            ageLastImproved = getAge();
        }

        return sharedFitness;
    }

    public float updateSharedFitness() {
        return updateFitness(Organism::getFitness);
    }

    public float updateFitness(final Context.GeneralSupport general) {
        return updateFitness(o -> o.updateFitness(general));
    }

    private void ensureOrganismsIsSorted() {
        if (!isOrganismsSorted) {
            isOrganismsSorted = true;
            Collections.sort(organisms);
        }
    }

    public List<Organism> removeUnfitToReproduce(final Context.SpeciationSupport speciation) {
        int size = organisms.size();

        if (size > 1) {
            int keep = speciation.params().fitToReproduce(size);
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

    public List<OrganismFactory> getOrganismsToBirth(final Context context, final int count) {
        List<OrganismFactory> organismsToBirth = new ArrayList<>();
        int size = organisms.size();

        for (int i = 0; i < count; i++) {
            ReproductionType reproductionType = context.speciation().nextReproductionType(size);

            OrganismFactory organismToBirth = switch (reproductionType) {
                case MATE_AND_MUTATE, MATE_ONLY -> {
                    Pair<Organism> organismCouple = context.random().nextUniquePair(organisms);

                    yield new MateBetweenOrganismsFactory(organismCouple.getLeft(), organismCouple.getRight(), reproductionType == ReproductionType.MATE_AND_MUTATE);
                }

                case MUTATE_ONLY -> {
                    Organism organism = context.random().nextItem(organisms);

                    yield new MutateSingleOrganismFactory(organism);
                }

                case CLONE -> {
                    Organism organism = context.random().nextItem(organisms);

                    yield new CloneSingleOrganismFactory(organism);
                }
            };

            organismsToBirth.add(organismToBirth);
        }

        return organismsToBirth;
    }

    public OrganismFactory getOrganismToBirth(final Context.RandomSupport random, final Species other) {
        if (organisms.isEmpty() || other.getOrganisms().isEmpty()) {
            return null;
        }

        Organism organism1 = random.nextItem(organisms);
        Organism organism2 = random.nextItem(other.organisms);

        return new MateBetweenOrganismsFactory(organism1, organism2, false);
    }

    public Organism selectMostElite() {
        ensureOrganismsIsSorted();

        return organisms.get(organisms.size() - 1);
    }

    public List<Organism> selectMostElites(final Context.SpeciationSupport speciation) {
        int size = organisms.size();
        int select = speciation.params().eliteToPreserve(size);

        if (select == 0) {
            return ImmutableList.of();
        }

        ensureOrganismsIsSorted();

        return organisms.subList(size - select, size);
    }

    public boolean shouldSurvive(final Context.SpeciationSupport speciation) {
        return getAge() - ageLastImproved < speciation.params().stagnationDropOffAge();
    }

    public List<Organism> restart(final Context.RandomSupport random) {
        int index = random.nextIndex(organisms.size());
        Organism representativeOrganismNew = organisms.remove(index);
        List<Organism> organismsOld = organisms;

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
