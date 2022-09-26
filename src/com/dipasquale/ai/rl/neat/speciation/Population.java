package com.dipasquale.ai.rl.neat.speciation;

import com.dipasquale.ai.rl.neat.NeatContext;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.ai.rl.neat.speciation.organism.OrganismFactory;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.FitnessEvaluationContext;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.ReproductionContext;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.SpeciesState;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.ChampionOrganismMissingException;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SelectionContext;
import com.dipasquale.data.structure.deque.NodeDeque;
import com.dipasquale.data.structure.deque.StandardNode;
import com.dipasquale.data.structure.deque.StandardNodeDeque;
import com.dipasquale.data.structure.iterable.IterableSupport;
import com.dipasquale.data.structure.set.DequeIdentitySet;
import com.dipasquale.data.structure.set.DequeSet;
import com.dipasquale.io.serialization.SerializableStateGroup;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Population {
    private static final Comparator<Species> SHARED_FITNESS_COMPARATOR = Comparator.comparing(Species::getSharedFitness);
    private final PopulationState populationState;
    private int size;
    private final DequeSet<Organism> undeterminedOrganisms;
    private final Queue<OrganismFactory> organismsToBirth;
    private final NodeDeque<Species, StandardNode<Species>> speciesNodes;
    @Getter
    private final OrganismActivator championOrganismActivator;

    public int getIteration() {
        return populationState.getIteration();
    }

    public int getGeneration() {
        return populationState.getGeneration();
    }

    public int getSpeciesCount() {
        return speciesNodes.size();
    }

    private void initializeUndeterminedOrganisms(final NeatContext context) {
        NeatContext.SpeciationSupport speciationSupport = context.getSpeciation();
        NeatContext.NodeGeneSupport nodeGeneSupport = context.getNodeGenes();

        for (int i = 0, c = size; i < c; i++) {
            Genome genome = speciationSupport.createGenesisGenome(context);
            Organism organism = new Organism(genome, populationState);

            organism.registerNodeGenes(nodeGeneSupport);
            undeterminedOrganisms.add(organism);
        }
    }

    public void initializeChampionOrganism(final Organism organism, final GenomeActivator genomeActivator) {
        championOrganismActivator.initialize(organism, genomeActivator);
    }

    private void initializeChampionOrganism(final Organism organism, final NeatContext context) {
        initializeChampionOrganism(organism.createClone(context.getConnectionGenes()), organism.provideActivator(context.getActivation()));
    }

    private Species createSpecies(final NeatContext.SpeciationSupport speciationSupport, final Organism representativeOrganism) {
        String id = speciationSupport.createSpeciesId();
        int stagnationDropOffAge = speciationSupport.getStagnationDropOffAge();

        return new Species(id, representativeOrganism, populationState, stagnationDropOffAge);
    }

    private StandardNode<Species> addSpecies(final Species species) {
        StandardNode<Species> speciesNode = speciesNodes.createUnbound(species);

        speciesNodes.add(speciesNode);

        return speciesNode;
    }

    private void assignOrganismsToSpecies(final NeatContext context) {
        Iterable<Organism> organismsBirthed = organismsToBirth.stream()
                .map(organismFactory -> organismFactory.create(context))
                ::iterator;

        NeatContext.SpeciationSupport speciationSupport = context.getSpeciation();
        OrganismMatchMaker matchMaker = new OrganismMatchMaker(speciationSupport);
        int maximumSpecies = speciationSupport.getMaximumSpecies();

        for (Organism organism : IterableSupport.concatenate(undeterminedOrganisms, organismsBirthed)) {
            for (StandardNode<Species> speciesNode : speciesNodes) {
                matchMaker.replaceIfBetterMatch(organism, speciesNodes.getValue(speciesNode));
            }

            if (speciesNodes.size() < maximumSpecies && !matchMaker.isBestMatchCompatible(populationState.getGeneration())) {
                addSpecies(createSpecies(speciationSupport, organism));
            } else {
                matchMaker.getBestMatch().add(organism);
            }

            matchMaker.clear();
        }

        undeterminedOrganisms.clear();
        organismsToBirth.clear();
        context.getMetrics().collectAllSpeciesCompositions(speciesNodes::flattenedIterator);
    }

    private void initialize(final NeatContext context) {
        initializeUndeterminedOrganisms(context);
        initializeChampionOrganism(undeterminedOrganisms.getFirst(), context);
        assignOrganismsToSpecies(context);
    }

    public static Population create(final NeatContext context) {
        int populationSize = context.getSpeciation().getPopulationSize();
        Population population = new Population(new PopulationState(), populationSize, new DequeIdentitySet<>(), new LinkedList<>(), new StandardNodeDeque<>(), new OrganismActivator());

        population.initialize(context);
        context.getActivation().initialize(populationSize);

        return population;
    }

    public void updateFitness(final NeatContext context) {
        context.getSpeciation().getFitnessEvaluationStrategy().evaluate(new FitnessEvaluationContext(context, speciesNodes));
        context.getMetrics().prepareNextFitnessEvaluation();
    }

    private SpeciesState createSpeciesState(final NeatContext.SpeciationSupport speciationSupport) {
        List<Species> all = new ArrayList<>();
        float[] totalFitness = {0f};

        List<Species> ranked = speciesNodes.flattenedStream()
                .peek(all::add)
                .peek(species -> totalFitness[0] += species.getSharedFitness())
                .filter(species -> !species.isStagnant())
                .sorted(SHARED_FITNESS_COMPARATOR)
                .collect(Collectors.toList());

        if (!ranked.isEmpty()) {
            return new SpeciesState(all, ranked, totalFitness[0]);
        }

        throw new PopulationExtinctionException("not enough organisms are allowed to reproduce the next generation");
    }

    private void reproduceThroughAllSpecies(final SelectionContext selectionContext) {
        NeatContext context = selectionContext.getParent();
        NeatContext.SpeciationSupport speciationSupport = context.getSpeciation();
        SpeciesState speciesState = createSpeciesState(speciationSupport);
        ReproductionContext reproductionContext = new ReproductionContext(context, speciesState, undeterminedOrganisms, organismsToBirth);

        context.getSpeciation().getReproductionStrategy().reproduce(reproductionContext);
    }

    private int countOrganismsInAllSpecies() {
        return speciesNodes.flattenedStream()
                .map(Species::getOrganisms)
                .map(List::size)
                .reduce(0, Integer::sum);
    }

    private int addOrganismCountEverywhere() {
        return undeterminedOrganisms.size() + organismsToBirth.size() + countOrganismsInAllSpecies();
    }

    public void evolve(final NeatContext context) {
        SelectionContext selectionContext = new SelectionContext(context, this);

        assert context.getSpeciation().getPopulationSize() == addOrganismCountEverywhere();
        assert undeterminedOrganisms.isEmpty();
        assert organismsToBirth.isEmpty() && context.getSpeciation().getDisposedGenomeIdCount() == 0;

        try {
            context.getSpeciation().getSelectionStrategy().select(selectionContext, speciesNodes);
        } catch (ChampionOrganismMissingException e) {
            throw new PopulationExtinctionException("the population has gone extinct", e);
        }

        assert undeterminedOrganisms.isEmpty();
        assert organismsToBirth.isEmpty();
        assert speciesNodes.size() == countOrganismsInAllSpecies();
        size = speciesNodes.size();
        reproduceThroughAllSpecies(selectionContext); // TODO: determine the order for this
        populationState.advanceGeneration();
        context.getActivation().advanceGeneration(size);
        context.getConnectionGenes().advanceGeneration();
        context.getMutation().advanceGeneration();
        context.getCrossOver().advanceGeneration();
        size = context.getSpeciation().advanceGeneration(size);
        context.getMetrics().prepareNextGeneration();
        assert context.getSpeciation().getPopulationSize() == addOrganismCountEverywhere();
        assert context.getSpeciation().getPopulationSize() == size;
        assert context.getSpeciation().getDisposedGenomeIdCount() == organismsToBirth.size();
        assignOrganismsToSpecies(context);
        assert context.getSpeciation().getPopulationSize() == addOrganismCountEverywhere();
        assert undeterminedOrganisms.isEmpty();
        assert organismsToBirth.isEmpty() && context.getSpeciation().getDisposedGenomeIdCount() == 0;
    }

    public void restart(final NeatContext context) {
        populationState.restart();
        context.getActivation().clear();
        context.getNodeGenes().clear();
        undeterminedOrganisms.clear();
        organismsToBirth.clear();
        context.getSpeciation().clear();
        speciesNodes.clear();
        context.getMetrics().prepareNextIteration();
        championOrganismActivator.reset();
        initialize(context);
    }

    public void save(final ObjectOutputStream outputStream)
            throws IOException {
        SerializableStateGroup stateGroup = new SerializableStateGroup();

        stateGroup.put("population.populationState", populationState);
        stateGroup.put("population.size", size);
        stateGroup.put("population.undeterminedOrganisms", undeterminedOrganisms);
        stateGroup.put("population.organismsToBirth", organismsToBirth);
        stateGroup.put("population.speciesNodes", speciesNodes);
        stateGroup.put("population.championOrganismActivator", championOrganismActivator);
        stateGroup.writeTo(outputStream);
    }

    public static Population create(final ObjectInputStream inputStream)
            throws IOException, ClassNotFoundException {
        SerializableStateGroup stateGroup = new SerializableStateGroup();

        stateGroup.readFrom(inputStream);

        PopulationState populationState = stateGroup.get("population.populationState");
        int size = stateGroup.get("population.size");
        DequeSet<Organism> undeterminedOrganisms = stateGroup.get("population.undeterminedOrganisms");
        Queue<OrganismFactory> organismsToBirth = stateGroup.get("population.organismsToBirth");
        NodeDeque<Species, StandardNode<Species>> speciesNodes = stateGroup.get("population.speciesNodes");
        OrganismActivator championOrganismActivator = stateGroup.get("population.championOrganismActivator");

        return new Population(populationState, size, undeterminedOrganisms, organismsToBirth, speciesNodes, championOrganismActivator);
    }
}