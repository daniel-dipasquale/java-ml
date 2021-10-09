package com.dipasquale.ai.rl.neat.speciation.core;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.speciation.organism.CloneSingleOrganismFactory;
import com.dipasquale.ai.rl.neat.speciation.organism.MateBetweenOrganismsFactory;
import com.dipasquale.ai.rl.neat.speciation.organism.MutateSingleOrganismFactory;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.ai.rl.neat.speciation.organism.OrganismFactory;
import com.dipasquale.common.Pair;
import com.dipasquale.data.structure.collection.Lists;
import com.dipasquale.data.structure.set.DequeSet;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    private boolean organismsSorted;
    @EqualsAndHashCode.Include
    private final PopulationState populationState;
    @Getter
    private float sharedFitness;
    private float maximumSharedFitness;
    private final int createdOnGeneration;
    private int improvedAtAge;

    Species(final String id, final Organism representativeOrganism, final PopulationState populationState) {
        this.id = id;
        this.initializeOrganisms(representativeOrganism);
        this.populationState = populationState;
        this.sharedFitness = 0f;
        this.maximumSharedFitness = 0f;
        this.createdOnGeneration = populationState.getGeneration();
        this.improvedAtAge = 0;
    }

    public Organism getRepresentative() {
        return representativeOrganism;
    }

    public List<Organism> getOrganisms() {
        return organismsReadOnly;
    }

    private void setOrganisms(final List<Organism> value) {
        organisms = value;
        organismsReadOnly = Collections.unmodifiableList(value);
    }

    private void initializeOrganisms(final Organism organism) {
        representativeOrganism = organism;
        setOrganisms(Lists.create(organism));
        organismsSorted = true;
    }

    public void add(final Organism organism) {
        organisms.add(organism);
        organismsSorted = false;
    }

    public int getAge() {
        return populationState.getGeneration() - createdOnGeneration;
    }

    private float updateAllFitness(final OrganismFitness organismFitness) {
        float fitnessTotal = organisms.stream()
                .map(organismFitness::getOrUpdate)
                .reduce(0f, Float::sum);

        float sharedFitnessFixed = fitnessTotal / organisms.size();

        sharedFitness = sharedFitnessFixed;

        if (Float.compare(sharedFitnessFixed, maximumSharedFitness) > 0) {
            maximumSharedFitness = sharedFitnessFixed;
            improvedAtAge = getAge();
        } // TODO: confirm if this version of the shared fitness matches the description of how the adjusted fitness should be working

        return sharedFitness;
    }

    public float updateSharedFitnessOnly(final Context.MetricSupport metricSupport) {
        updateAllFitness(Organism::getFitness);
        metricSupport.collectFitness(this);

        return sharedFitness;
    }

    public float updateAllFitness(final Context context) {
        updateAllFitness(o -> o.updateFitness(this, context));
        context.metrics().collectFitness(this);

        return sharedFitness;
    }

    private List<Organism> ensureOrganismsIsSorted() {
        if (!organismsSorted) {
            organismsSorted = true;
            Collections.sort(organisms);
        }

        return organisms;
    }

    public List<Organism> removeUnfitToReproduce(final Context.SpeciationSupport speciationSupport) {
        int size = organisms.size();

        if (size > 1) {
            int keep = speciationSupport.params().fitToReproduce(size);
            int remove = size - keep;

            if (remove > 0) {
                List<Organism> organismsRemoved = ensureOrganismsIsSorted().subList(0, remove);

                setOrganisms(organisms.subList(remove, size));

                return organismsRemoved;
            }
        }

        return List.of();
    }

    public List<OrganismFactory> reproduce(final Context context, final int count) {
        List<OrganismFactory> organismsToBirth = new ArrayList<>();
        int size = organisms.size();

        for (int i = 0; i < count; i++) {
            ReproductionType reproductionType = context.speciation().generateReproductionType(size);

            OrganismFactory organismToBirth = switch (reproductionType) {
                case MATE_AND_MUTATE, MATE_ONLY -> {
                    Pair<Organism> organismCouple = context.random().generateItemPair(organisms);

                    yield new MateBetweenOrganismsFactory(organismCouple.getLeft(), organismCouple.getRight(), reproductionType == ReproductionType.MATE_AND_MUTATE);
                }

                case MUTATE_ONLY -> {
                    Organism organism = context.random().generateItem(organisms);

                    yield new MutateSingleOrganismFactory(organism);
                }

                case CLONE -> {
                    Organism organism = context.random().generateItem(organisms);

                    yield new CloneSingleOrganismFactory(organism);
                }
            };

            organismsToBirth.add(organismToBirth);
        }

        return organismsToBirth;
    }

    public OrganismFactory reproduce(final Context.RandomSupport randomSupport, final Species other) {
        if (organisms.isEmpty() || other.organisms.isEmpty()) {
            return null;
        }

        Organism organism1 = randomSupport.generateItem(organisms);
        Organism organism2 = randomSupport.generateItem(other.organisms);

        return new MateBetweenOrganismsFactory(organism1, organism2, false);
    }

    public Organism getChampionOrganism() {
        return ensureOrganismsIsSorted().get(organisms.size() - 1);
    }

    public List<Organism> getFittestOrganisms(final Context.SpeciationSupport speciationSupport, final boolean includeRepresentative) {
        int size = organisms.size();
        int select = speciationSupport.params().elitesToPreserve(size, includeRepresentative);

        if (select == 0) {
            return List.of();
        }

        return ensureOrganismsIsSorted().subList(size - select, size);
    }

    public int getStagnationPeriod() {
        return getAge() - improvedAtAge;
    }

    public boolean isStagnant(final int stagnationDropOffAge) {
        return getStagnationPeriod() >= stagnationDropOffAge;
    }

    public boolean isStagnant(final Context.SpeciationSupport speciationSupport) {
        return isStagnant(speciationSupport.params().stagnationDropOffAge());
    }

    public boolean shouldSurvive() {
        return organisms.size() > 1;
    }

    public List<Organism> restart(final Context.RandomSupport randomSupport, final DequeSet<Organism> organismsTaken) {
        List<Organism> organismsFixed = organisms.stream()
                .filter(o -> !organismsTaken.contains(o))
                .collect(Collectors.toList());

        int index = randomSupport.generateIndex(organismsFixed.size());
        Organism representativeOrganismFixed = organismsFixed.remove(index);

        initializeOrganisms(representativeOrganismFixed);
        sharedFitness = 0f;

        return organismsFixed;
    }

    @FunctionalInterface
    private interface OrganismFitness {
        float getOrUpdate(Organism organism);
    }
}
